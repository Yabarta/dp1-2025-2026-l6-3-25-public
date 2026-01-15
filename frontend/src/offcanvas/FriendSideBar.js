import React, { useState, useEffect } from 'react';
import { Table, Button, Offcanvas, ButtonGroup, Toast } from 'reactstrap';
import useFetchState from "../util/useFetchState";
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useNavigate, Link } from 'react-router-dom';
import api from '../services/api';

function FriendsSidebar({ isOpen, toggle, username, jwt, id}) {


    const [nombreBuscadoFriend , setNombreFriend] = useState("");
    const [nombreBuscadoPlayer , setNombrePlayer] = useState("");
    const [friends, setFriends] = useState([]);
    const [change , setChange] = useState(false); // Estado para controlar cambios
    const [changeFriend, setChangeFriend] = useState(false); // Estado para controlar cambios en amigos
    const [players, setPlayers] = useFetchState(
        [],
        `/api/v1/players`,
    );
    const [friendMatches, setFriendMatches] = useState([]);
    const[requester, setRequester] = useState([]);
    const [requests, setRequests] = useState([]);
    const [inLobby, setInLobby] = useState(false);

    const [path , setPath] = useState(window.location.pathname);

    const [showToast, setShowToast] = useState(false);
    const [invitationData, setInvitationData] = useState(null);
    const [invitationId, setInvitationId] = useState(null);

    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const [stompClient, setStompClient] = useState([])

    const [lobbyId , setLobbyId] = useState(null);

    const [disabledButtons, setDisabledButtons] = useState({});

    if (window.location.pathname !== path) {
        setPath(window.location.pathname);
    }

    useEffect(() => {
    const intervalChange = setInterval(() => {
        setChangeFriend(c => !c);
    }, 10000);

    // Limpieza: detiene el reloj si el usuario cierra el sidebar
    return () => clearInterval(intervalChange);
}, []);

    useEffect(() => {
        if (window.location.pathname.includes("/lobby/")) {
            setInLobby(true);
            setLobbyId(window.location.pathname.replace("/lobby/", ""));
        } else {
            setInLobby(false);
            setLobbyId(null);
        }
    }, [path]);

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

    useEffect(() => {
    const socket = new SockJS('http://localhost:8080/ws');
    const client = Stomp.over(socket);

    client.connect({}, () => {
        client.subscribe(`/topic/invitations/${id}`, (message) => {
            if (!inLobby) {
            console.log("Invitación recibida");
            const data = JSON.parse(message.body); 
            setInvitationData(data.username);
            setInvitationId(data.lobbyId);
            setShowToast(true);

            setTimeout(() => {
                setShowToast(false);
            }, 30000);
            }
        });
    });

    setStompClient(client);
    return () => {client.disconnect()};
}, [id, inLobby]);

    // Handlers
    const handleDelete = async(id) => 
        {
        const response = await fetch(`/api/v1/friends/${id}`, {method: 'DELETE',
                            headers: {
                                "Authorization": `Bearer ${jwt}`,
                                "Content-Type": "application/json"
                                }
                        });
        if (response.ok) {  
        stompClient.send('/app/friend', {}, );
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
            const response = await fetch(`/api/v1/players/friends/${id}`,
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

    const handleInvite = async(idFriend , props) =>
    {   
        stompClient.send(`/app/invite/${idFriend}`, {}, JSON.stringify(props));
        setDisabledButtons(prev => ({ ...prev, [idFriend]: true }));
        setTimeout(() => {
        setDisabledButtons(prev => {
            const newState = { ...prev };
            delete newState[idFriend]; // Eliminamos la propiedad para desbloquearlo
            return newState;
        });
    }, 50000);
    };

    const handleJoinLobby = async (lobbyId, code) => {
        setIsLoading(true);
        setError(null);
        try {
            await api.put(`/api/v1/matches/${lobbyId}${code ? `?code=${code}` : ''}`);
            navigate(`/lobby/${lobbyId}`);
        } catch (err) {
            console.error('Error joining lobby', err);
            setError('No se pudo unir a la sala.');
        } finally {
            setIsLoading(false);
        }
        };
        
    const handleSpectate = async (idPlayer , idFriend) => 
            {
                
                try {
                    await api.get(`/api/v1/friends/espectate?idPlayer=${idPlayer}&idFriend=${idFriend}`, {
                    headers: {
                        "Authorization": `Bearer ${jwt}`,
                        "Content-Type": "application/json"
                    }
                });
                } catch (error) {
                    console.error(error);
                }};

//Friend Match List

useEffect(() => 
    {
        if (!id) return;

        const fetchFriendsMatches = async () => 
        {
            try {
                // Petición para obtener la lista de amigos
                const response = await fetch(`/api/v1/friends/espectate?idPlayer=${id}`,
                {headers: {
                        "Authorization": `Bearer ${jwt}`,
                        "Content-Type": "application/json"
                    }
                }
                );
                // Comprobar si la respuesta es correcta
                if (!response.ok) throw new Error("Error en la petición");
                // Convertir la respuesta a JSON
                const data = await response.json();
                // Actualizar el estado con la lista de amigos
                setFriendMatches(data);
                
            } catch (error) {
                console.error(error);
            }
        };

        // Llamar a la función para obtener la lista de amigos
        fetchFriendsMatches();
    },
    [username , change , changeFriend]);

//Friend List

    const filterFriend = friends.filter(
                                        (friend) => friend.requester.nickname === username
                                        ? friend.receiver.nickname.toLowerCase().includes(nombreBuscadoFriend.toLowerCase())
                                        : friend.requester.nickname.toLowerCase().includes(nombreBuscadoFriend.toLowerCase())
                                        );

    const friendList = filterFriend.map((friend) =>
        {

            const friendDisplay = (friend.receiver.nickname === username) 
                ? friend.requester
                : friend.receiver;

            const matchData = friendMatches.find(m => m.player1.id === friendDisplay.id || m.player2.id === friendDisplay.id);

            return (
                    <tr key={friend.id}>
                        
                        <td>{friendDisplay.nickname}</td> 
                        <td>{friendDisplay.isOnline ? (friendDisplay.isCurrentlyInMatch ? "En partida" : "Conectado") : "Desconectado"}</td>
                        <td>
                            <ButtonGroup>
                                { inLobby && friendDisplay.isOnline &&
                                <Button
                                    style={{justifyContent: 'flex-end', backgroundColor: 'green'}}
                                    onClick={() => handleInvite(friendDisplay.id , {lobbyId , username})}
                                    disabled={!!disabledButtons[friendDisplay.id]} >

                                    {disabledButtons[friendDisplay.id] ? 'Enviado' : 'Invitar a partida'}
                                </Button>}
                                <Button
                                    style={{justifyContent: 'flex-end', backgroundColor: 'red'}}
                                    onClick={() => {handleDelete(friend.id);}}>
                                    Eliminar Amigo
                                </Button>
                                { friendDisplay.isCurrentlyInMatch && matchData &&
                                <Link
                                        className="auth-button blue"
                                        style={{ padding: '0.5rem 1rem', fontSize: '0.8rem', minWidth: 'auto' }}
                                        to={"/game/" + matchData.id}>
                                        Espectear juego
                                    </Link>}
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
    [username , change , changeFriend]);

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

    // Online

    useEffect(() => {
    if (!jwt) return;
    if (!id) return;
    const detection = async () => {
      try {
        await fetch("/api/v1/players/onlineDetection/" + id , 
            {method: 'PUT',
            headers: {
                "Authorization": `Bearer ${jwt}`,
                "Content-Type": "application/json"
            }
        }
        );
      } catch (err) {
        console.error("Error en la detección de presencia", err);
      }
    };

    
    detection();
    const intervalId = setInterval(detection, 5000);
    return () => clearInterval(intervalId);
  }, [jwt , id, stompClient]);

    return (<>
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
                <th>Estado</th>
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
    <div style={{ position: 'fixed', top: '20px', right: '20px', zIndex: 9999, width: '300px' }}>
    <Toast isOpen={showToast} className="bg-primary text-white">
        <div className="p-3 d-flex justify-content-between align-items-center">
            <strong>Nueva Invitación</strong>
            <Button close onClick={() => setShowToast(false)} />
        </div>
        <div className="p-3 bg-dark text-white border-top">
            <p>Has recibido una invitación de: {invitationData}</p>
            <div className="d-flex justify-content-end gap-2">
                <Button color="success" size="sm" onClick={() => {handleJoinLobby(invitationId); setShowToast(false);}}>
                    Aceptar
                </Button>
                <Button color="danger" size="sm" onClick={() => setShowToast(false)}>
                    Ignorar
                </Button>
            </div>
        </div>
    </Toast>
    </div>
    </>
);
}
export default FriendsSidebar;