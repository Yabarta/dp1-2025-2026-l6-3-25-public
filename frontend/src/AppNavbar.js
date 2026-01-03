import React, { useState, useEffect } from 'react';
import { Navbar, NavbarBrand, NavLink, NavItem, Nav, NavbarText, NavbarToggler, Collapse, Button, Offcanvas, ButtonGroup } from 'reactstrap';
import { Link } from 'react-router-dom';
import tokenService from './services/token.service';
import jwt_decode from "jwt-decode";
import mitosisImg from './static/images/mitosis.png';
import useFetchState from "./util/useFetchState";
import {Table } from "reactstrap";

function AppNavbar() {
    const [roles, setRoles] = useState([]);
    const [username, setUsername] = useState("");
    const jwt = tokenService.getLocalAccessToken();
    const [collapsed, setCollapsed] = useState(true);
    const [isOpenFriends, setIsOpenFriends] = useState(false);
    const [nombreBuscadoFriend , setNombreFriend] = useState("");
    const [nombreBuscadoPlayer , setNombrePlayer] = useState("");
    const [friends, setFriends] = useState([]);
    const [change , setChange] = useState(false);


    //Friend List

    const filterFriend = friends.filter((friend) => friend.requester.nickname === username
    ?friend.receiver.nickname.toLowerCase().includes(nombreBuscadoFriend.toLowerCase())
    :friend.requester.nickname.toLowerCase().includes(nombreBuscadoFriend.toLowerCase())
    );

const friendList = filterFriend.map((friend) => {
    const friendDisplayName = (friend.receiver.nickname === username) 
        ? friend.requester.nickname
        : friend.receiver.nickname;

    return (
        <tr key={friend.id}>
            {/* Aquí mostramos el nombre calculado */}
            <td>{friendDisplayName}</td> 
            
            <td>
                <ButtonGroup>
                    <Button
                        style={{justifyContent: 'flex-end', backgroundColor: 'red'}}
                        onClick={() => {handleDelete(friend.id);}}>
                        Delete Friend
                    </Button>
                </ButtonGroup>
            </td>
        </tr>
    );
});

    useEffect(() => {
        if (jwt) {
            setRoles(jwt_decode(jwt).authorities);
            setUsername(jwt_decode(jwt).sub);
        }
    }, [jwt]);

    useEffect(() => {
    if (!username) return;

    const fetchFriends = async () => {
        try {
            // A. Hacemos la petición
            const response = await fetch(`/api/v1/players/${username}/friends`);
            if (!response.ok) throw new Error("Error en la petición");
            
            const data = await response.json();

            // B. Aquí guardamos los amigos en el estado
            setFriends(data);
            
        } catch (error) {
            console.error(error);
        }
    };

    fetchFriends();

}, [username , change]);

    // Request List

    const [requests, setRequests] = useState([]);

    const handleDelete = async(id) => {
        const response = await fetch(`api/v1/friends/${id}`, {method: 'DELETE', 
                            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${jwt}`
            }
                        });
        setChange(!change);
    };

    const handleAccept = async(id) => {
        const response = await fetch(`api/v1/players/friends/${id}`, {method: 'PUT', 
                            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${jwt}`
            }
                        });
        setChange(!change);
    };

    const requestList = requests.map((request) => {

    return (
        <tr key={request.id}>
            <td>{request.requester.nickname}</td> 
            
            <td>
                <ButtonGroup>
                    <Button
                        style={{justifyContent: 'flex-end', backgroundColor: 'green'}}
                        onClick={() => {handleAccept(request.id);}}>
                        Accept
                    </Button>
                    <Button
                        style={{justifyContent: 'flex-end', backgroundColor: 'red'}}
                        onClick={() => {handleDelete(request.id);}}>
                        Decline
                    </Button>
                </ButtonGroup>
            </td>
        </tr>
    );
});

useEffect(() => {
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

}, [username , change]);

    // Player List

    const [players, setPlayers] = useFetchState(
        [],
        `/api/v1/players`,
    );

    const[requester, setRequester] = useState([]);

    useEffect(() => {
    if (!username) return;

    const fetchRequester = async () => {
        try {
            const response = await fetch(`/api/v1/players/${username}/requester`);
            if (!response.ok) throw new Error("Error en la petición");
            
            const data = await response.json();
            setRequester(data);
            
        } catch (error) {
            console.error(error);
        }
    };

    fetchRequester();

}, [username , change]);

    const filterPlayers = players.filter((player) => {
        return player.nickname.toLowerCase().includes(nombreBuscadoPlayer.toLowerCase()) && 
        player.nickname !== username && nombreBuscadoPlayer!== "" && 
        !friendList.map(friend => friend.props.children[0].props.children).includes(player.nickname) &&  
        !requestList.map(request => request.props.children[0].props.children).includes(player.nickname);
    }
    );

    const handleCreate = async(requester, receiver) => {
        const payload = { requester, receiver };

    const response = await fetch(`/api/v1/players/friends`, { 
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${jwt}`
        },
        body: JSON.stringify(payload)
    });
        setChange(!change);
    };

    const userList = filterPlayers.map((player) => {

        let solicitudEnviada = false;

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
                {solicitudEnviada ? "Sent Request" : "Send Friends Request"}
            </Button>
            </td>
        </tr>
            );
        });

    const toggleNavbar = () => setCollapsed(!collapsed);

    const toggleMenu = () => setIsOpenFriends(!isOpenFriends);

    let adminLinks = <></>;
    let ownerLinks = <></>;
    let userLinks = <></>;
    let userLogout = <></>;
    let publicLinks = <></>;

    roles.forEach((role) => {
        if (role === "ADMIN") {
            adminLinks = (
                <>
                    <NavItem>
                        <NavLink style={{ color: "white" }} tag={Link} to="/users">Users</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "white" }} tag={Link} to="/currentGames">Current Games</NavLink>
                    </NavItem>
                    
                </>
            )
        }
    })

    if (!jwt) {
        publicLinks = (
            <>
    
                <NavItem>
                    <NavLink style={{ color: "white" }} id="login" tag={Link} to="/login">Login</NavLink>
                </NavItem>
                <NavItem>
                    <NavLink style={{ color: "white" }} id="register" tag={Link} to="/register">Register</NavLink>
                </NavItem>
            </>
        )
    } else {
        userLinks = (
            <>
                <NavItem>
                    <Button style={{ color: "white" }} id="friends-btn" onClick={toggleMenu} className="btn btn-link nav-link">Friends</Button>
                </NavItem>
                <NavItem>
                    <NavLink style={{ color: "white" }} id="comparator" tag={Link} to="/comparator">Comparador</NavLink>
                </NavItem>
                <NavItem>
                    <NavLink style={{ color: "white" }} id="statistics" tag={Link} to="/ranking">Ranking</NavL
                </NavItem>
                <NavItem>
                    <NavLink style={{ color: "white" }} id="leaderboards" tag={Link} to="/leaderboards">Leaderboards</NavLink>    
                </NavItem>

            </>
        )
        userLogout = (
            <>
                <NavbarText style={{ color: "white" }} className="justify-content-end">{username}</NavbarText>
                <NavItem className="d-flex">
                    <NavLink style={{ color: "white" }} id="logout" tag={Link} to="/logout">Logout</NavLink>
                </NavItem>
            </>
        )

    }

    return (
        <div>
            <Navbar expand="md" dark color="dark">
                <NavbarBrand tag={Link} to="/">
                    <img alt="logo" src={mitosisImg} style={{ height: 40, width: 40, paddingRight: 8 }} />
                    Petris
                </NavbarBrand>
                <NavbarToggler onClick={toggleNavbar} className="ms-2" />
                <Collapse isOpen={!collapsed} navbar>
                    <Nav className="me-auto mb-2 mb-lg-0" navbar>
                        {userLinks}
                        {adminLinks}
                        {ownerLinks}
                    </Nav>
                    <Nav className="ms-auto mb-2 mb-lg-0" navbar>
                        {publicLinks}
                        {userLogout}
                    </Nav>
                </Collapse>
            </Navbar>

            <Offcanvas isOpen={isOpenFriends} onClose={toggleMenu} direction='start'  style={{width: "33%" , overflowY: "scroll", borderLeftColor: "white"}} className="bg-dark text-white">
                <div className="barra-busqueda-Friends d-flex justify-content-center align-items-center position-relative p-3">
                    <input type="search" value={nombreBuscadoFriend} onChange={(usuario) => setNombreFriend(usuario.target.value)} placeholder="Buscar usuario" />
                    <Button color="secondary" onClick={toggleMenu} style={{width: '10%'}} className= "position-absolute end-0">X</Button>
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
                <th>Action</th>
            </tr>
        </thead>
            <tbody>{friendList}</tbody>
        </Table>
        </div>
            </Offcanvas>

            <Offcanvas isOpen={isOpenFriends} onClose={toggleMenu} direction='end' style={{width: "33%", overflowY: "scroll"}} backdrop={false} className="bg-dark text-white">
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
                <th>Action</th>
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
                <th>Request</th>
                <th>Action</th>
            </tr>
        </thead>
            <tbody>{requestList}</tbody>
        </Table>
        </div>
            </Offcanvas>
            
        </div>
    );
}

export default AppNavbar;