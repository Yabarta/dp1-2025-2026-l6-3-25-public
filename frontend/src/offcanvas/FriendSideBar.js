import React, { useState, useEffect } from 'react';
import { Table, Button, Offcanvas, ButtonGroup } from 'reactstrap';
import useFetchState from "../util/useFetchState";
import useWebSocket from '../hooks/useWebSocket';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

function FriendsSidebar({ isOpen, toggle, username, jwt}) {


    const [nombreBuscadoFriend , setNombreFriend] = useState("");
    const [nombreBuscadoPlayer , setNombrePlayer] = useState("");
    const [friends, setFriends] = useState([]);
    const [change , setChange] = useState(false); // Estado para controlar cambios
    const [players, setPlayers] = useFetchState(
        [],
        `/api/v1/players`,
    );
    const[requester, setRequester] = useState([]);
    const [requests, setRequests] = useState([]);
    const [inLobby, setInLobby] = useState(false);

    const [stompClient, setStompClient] = useState([])

    useEffect(() => {
            const socket = new SockJS('http://localhost:8080/ws');
            const client = Stomp.over(socket);
    
            client.connect({}, () => {
                client.subscribe('/topic/friends', () => {
                    setChange(c => !c)
                    console.log("Actualizando lista de amigos")
                })
            })
            setStompClient(client)
            return () =>{
                client.disconnect()
            }
        }, [])

    // Handlers
    const handleDelete = async(id) => 
        {
        const response = await fetch(`api/v1/friends/${id}`, {method: 'DELETE',
                            headers: {
                                "Authorization": `Bearer ${jwt}`,
                                "Content-Type": "application/json"
                                }
                        });
        if (response.ok) {  
        stompClient.send('/app/friend', {}, JSON.stringify(""));
    }
        };

    const handleCreate = async(requester, receiver) => {
        
        const payload = { requester, receiver };

        const response = await fetch(`/api/v1/players/friends`, {
                method: 'POST',
                headers: {
                            "Authorization": `Bearer ${jwt}`,
                            "Content-Type": "application/json"
                        },
                body: JSON.stringify(payload)
            });
        if (response.ok) {  
        stompClient.send('/app/friend', {}, JSON.stringify(""));
    }
    };

    const handleAccept = async(id) =>
        {
            const response = await fetch(`api/v1/players/friends/${id}`,
                                {method: 'PUT',
                                headers: { 
                                            "Authorization": `Bearer ${jwt}`,
                                            "Content-Type": "application/json"
                                        }
                                });
            if (response.ok) {  
                stompClient.send('/app/friend', {}, JSON.stringify(""));
            }
        };

//Friend List

    const filterFriend = friends.filter(
                                        (friend) => friend.requester.nickname === username
                                        ? friend.receiver.nickname.toLowerCase().includes(nombreBuscadoFriend.toLowerCase())
                                        : friend.requester.nickname.toLowerCase().includes(nombreBuscadoFriend.toLowerCase())
                                        );

    const friendList = filterFriend.map((friend) =>
        {
            const friendDisplayName = (friend.receiver.nickname === username) 
                ? friend.requester.nickname
                : friend.receiver.nickname;

            return (
                    <tr key={friend.id}>
                        <td>{friendDisplayName}</td> 
                        
                        <td>
                            <ButtonGroup>
                                { inLobby &&
                                <Button
                                    style={{justifyContent: 'flex-end', backgroundColor: 'green'}}
                                    onClick={() => {}}>
                                    
                                    Invitar a partida
                                </Button>}
                                <Button
                                    style={{justifyContent: 'flex-end', backgroundColor: 'red'}}
                                    onClick={() => {handleDelete(friend.id);}}>
                                    Eliminar Amigo
                                </Button>
                            </ButtonGroup>
                        </td>
                    </tr>
                    );
        });

    useEffect(() => 
    {
        if (!username) return;

        const fetchFriends = async () => 
        {
            try {
                // Petición para obtener la lista de amigos
                const response = await fetch(`/api/v1/players/${username}/friends`);
                // Comprobar si la respuesta es correcta
                if (!response.ok) throw new Error("Error en la petición");
                // Convertir la respuesta a JSON
                const data = await response.json();
                // Actualizar el estado con la lista de amigos
                setFriends(data);
                
            } catch (error) {
                console.error(error);
            }
        };

        // Llamar a la función para obtener la lista de amigos
        fetchFriends();
    },
    [username , change]);

    // Request List

    const requestList = requests.map((request) => 
    {
        return (
            <tr key={request.id}>
                <td>{request.requester.nickname}</td> 
                <td>
                    <ButtonGroup>
                        <Button
                            style={{justifyContent: 'flex-end', backgroundColor: 'green'}}
                            onClick={() => {handleAccept(request.id);}}>
                            Aceptar
                        </Button>
                        <Button
                            style={{justifyContent: 'flex-end', backgroundColor: 'red'}}
                            onClick={() => {handleDelete(request.id);}}>
                            Rechazar
                        </Button>
                    </ButtonGroup>
                </td>
            </tr>
        );
    });

    useEffect(() => 
    {
        if (!username) return;

        const fetchRequest = async () => {
            try {
                const response = await fetch(`/api/v1/players/${username}/requests`);
                if (!response.ok) throw new Error("Error en la petición");
                const data = await response.json();
                setRequests(data);
                
            } catch (error) {
                console.error(error);
            }
        };

        fetchRequest();

    },
    [username , change]);

    // Player List

    useEffect(() => 
    {
        if (!username) return;

        const fetchRequester = async () => {
            try {
                // Petición para ver las solicitudes enviadas por el usuario
                const response = await fetch(`/api/v1/players/${username}/requester`);
                if (!response.ok) throw new Error("Error en la petición");
                
                const data = await response.json();
                setRequester(data);
                
            } catch (error) {
                console.error(error);
            }
        };

        fetchRequester();

    },
    [username , change]);

    const filterPlayers = players.filter((player) => {
        return player.nickname.toLowerCase().includes(nombreBuscadoPlayer.toLowerCase()) && 
        player.nickname !== username && nombreBuscadoPlayer!== "" && 
        !friendList.map(friend => friend.props.children[0].props.children).includes(player.nickname) &&  // Verifica que no esté en la lista de amigos, friend.props.children[0].props.children accede al nickname del amigo
        !requestList.map(request => request.props.children[0].props.children).includes(player.nickname); // Verifica que no esté en la lista de solicitudes, request.props.children[0].props.children accede al nickname del solicitante
    }
    );

    

    const userList = filterPlayers.map((player) => {

        let solicitudEnviada = false;

        // Verifica si ya se ha enviado una solicitud a este jugador
        if (requester.some(req => req.receiver.nickname === player.nickname)) {
            solicitudEnviada = true;
        }

        return (
        <tr key={player.id}>
            <td>{player.nickname}</td>
            <td>
            <Button
                color="primary"
                style={{justifyContent: 'flex-end'}}
                onClick={() => {handleCreate(username, player.nickname)}}
                disabled={solicitudEnviada}>
                {solicitudEnviada ? "Solicitud enviada" : "Enviar solicitud"}
            </Button>
            </td>
        </tr>
            );
        });

    return (
            <div>
            <Offcanvas isOpen={isOpen} onClose={toggle} direction='start'  style={{width: "33%" , overflowY: "scroll", borderLeftColor: "white"}} className="bg-dark text-white">
                <div className="barra-busqueda-Friends d-flex justify-content-center align-items-center position-relative p-3">
                    <input type="search" value={nombreBuscadoFriend} onChange={(usuario) => setNombreFriend(usuario.target.value)} placeholder="Buscar usuario" />
                    <Button color="secondary" onClick={toggle} style={{width: '10%'}} className= "position-absolute end-0">X</Button>
                </div>
                <div>
        <Table borderless className="m-0" 
        style={{
        "--bs-table-bg": "transparent",
        "--bs-table-color": "white"
    }}>
        <thead>
            <tr>
                <th>Username</th>
                <th>Acciones</th>
            </tr>
        </thead>
            <tbody>{friendList}</tbody>
        </Table>
        </div>
            </Offcanvas>

            <Offcanvas isOpen={isOpen} onClose={toggle} direction='end' style={{width: "33%", overflowY: "scroll"}} backdrop={false} className="bg-dark text-white">
                <div className="barra-busqueda-Players d-flex justify-content-center align-items-center position-relative p-3" >
                    <input type="search" value={nombreBuscadoPlayer} onChange={(usuario) => setNombrePlayer(usuario.target.value)} placeholder="Buscar usuario" />
                </div>
                <div style={{ height: "45%", overflowY: "auto", borderBottom: "1px solid #444" }}>
        <Table aria-label="users" className="mt-0"
        style={{
        "--bs-table-bg": "transparent",
        "--bs-table-color": "white"
    }}>
        <thead>
            <tr>
                <th>Username</th>
                <th>Acciones</th>
            </tr>
        </thead>
            <tbody>{userList}</tbody>
        </Table>
        </div>

        <div style={{ height: "45%", overflowY: "auto"}}>
        <Table aria-label="request" className="mt-4"
        style={{
        "--bs-table-bg": "transparent",
        "--bs-table-color": "white"
    }}>
        <thead>
            <tr>
                <th>Solicitudes</th>
                <th>Acciones</th>
            </tr>
        </thead>
            <tbody>{requestList}</tbody>
        </Table>
        </div>
            </Offcanvas>
            </div>
    );
}
export default FriendsSidebar;