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
    const [nombreBuscado , setNombre] = useState("");
    const [friends, setFriends] = useFetchState(
        [],
        `/api/v1/players/${username}/friends`,
    );
    
    const filterFriends = friends.filter((user) =>
    user.nickname.toLowerCase().includes(nombreBuscado.toLowerCase())
    );

    const userFriends = filterFriends.map((user) => {
        return (
        <tr key={user.id}>
            <td>{user.nickname}</td>
            <td>
                <ButtonGroup>
            <Button
                style={{justifyContent: 'flex-end', backgroundColor: 'green'}}>
                Invite
            </Button>
            <Button
                style={{justifyContent: 'flex-end', backgroundColor: 'red'}}>
                Delete Friend
            </Button>
            </ButtonGroup>
            </td>
            
        </tr>
            );
        });

    const [request, setRequest] = useFetchState(
        [],
        `/api/v1/players/${username}/request`,
    );

    const requestList = request.map((user) => {
        return (
        <tr key={user.id}>
            <td>{user.nickname}</td>
            <td>
                <ButtonGroup>
            <Button
                style={{justifyContent: 'flex-end', backgroundColor: 'green'}}>
                Accept
            </Button>
            <Button
                style={{justifyContent: 'flex-end', backgroundColor: 'red'}}>
                Reject
            </Button>
            </ButtonGroup>
            </td>
            
        </tr>
            );
        });

    const [players, setPlayers] = useFetchState(
        [],
        `/api/v1/players`,
    );

    const userList = players.map((player) => {
        return (
        <tr key={player.id}>
            <td>{player.nickname}</td>
            <td>
            <Button
                color="primary"
                style={{justifyContent: 'flex-end'}}>
                Send Friends Request
            </Button>
            </td>
        </tr>
            );
        });

    const toggleNavbar = () => setCollapsed(!collapsed);

    function setname(nombreDeUsuario){setNombre(nombreDeUsuario);}

    const toggleMenu = () => setIsOpenFriends(!isOpenFriends);

    useEffect(() => {
        if (jwt) {
            setRoles(jwt_decode(jwt).authorities);
            setUsername(jwt_decode(jwt).sub);
        }
    }, [jwt])


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
                    <NavLink style={{ color: "white" }} id="docs" tag={Link} to="/docs">Docs</NavLink>
                </NavItem>
                <NavItem>
                    <NavLink style={{ color: "white" }} id="plans" tag={Link} to="/plans">Pricing Plans</NavLink>
                </NavItem>
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
            </>
        )
        userLogout = (
            <>
                <NavItem>
                    <NavLink style={{ color: "white" }} id="docs" tag={Link} to="/docs">Docs</NavLink>
                </NavItem>
                <NavItem>
                    <NavLink style={{ color: "white" }} id="plans" tag={Link} to="/plans">Pricing Plans</NavLink>
                </NavItem>
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

            <Offcanvas isOpen={isOpenFriends} onClose={toggleMenu} direction='start' style={{width: "33%" , backgroundColor: "#f8f9fa", overflowY: "scroll"}} >
                <div class="barra-busqueda">
                    <input type="search" value={nombreBuscado} onChange={(usuario) => setname(usuario.target.value)} placeholder="Buscar usuario" />
                </div>
                <div>
        <Table aria-label="friends" className="mt-4">
        <thead>
            <tr>
                <th>Username</th>
                <th>Action</th>
            </tr>
        </thead>
            <tbody>{userFriends}</tbody>
        </Table>
        </div>
            </Offcanvas>
            <Offcanvas isOpen={isOpenFriends} onClose={toggleMenu} direction='end' style={{width: "33%"}} backdrop={false} >
                <Button color="secondary" onClick={toggleMenu} style={{width: '10%'}}>X</Button>

                <div>
        <Table aria-label="users" className="mt-4" stickyHeader>
        <thead>
            <tr>
                <th>Username</th>
                <th>Action</th>
            </tr>
        </thead>
            <tbody>{userList}</tbody>
        </Table>
        <Table aria-label="request" className="mt-4">
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