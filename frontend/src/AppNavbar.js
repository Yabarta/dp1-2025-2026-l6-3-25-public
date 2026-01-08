import React, { useState, useEffect } from 'react';
import { Navbar, NavbarBrand, NavLink, NavItem, Nav, NavbarText, NavbarToggler, Collapse, Button, Offcanvas, ButtonGroup } from 'reactstrap';
import { Link } from 'react-router-dom';
import tokenService from './services/token.service';
import jwt_decode from "jwt-decode";
import mitosisImg from './static/images/mitosis.png';
import FriendsSidebar from './offcanvas/FriendSideBar';

function AppNavbar() {
    const [roles, setRoles] = useState([]);
    const [username, setUsername] = useState("");
    const jwt = tokenService.getLocalAccessToken();
    const [collapsed, setCollapsed] = useState(true);
    const [isOpenFriends, setIsOpenFriends] = useState(false);
                
    const toggleNavbar = () => setCollapsed(!collapsed);
    const toggleMenu = () => setIsOpenFriends(!isOpenFriends);
    const [id, setId] = useState("");
    // Deteccion de roles y username

    useEffect(() => {
        if (jwt) {
            setRoles(jwt_decode(jwt).authorities);
            setUsername(jwt_decode(jwt).sub);
    }
    }, [jwt]);

    // Deteccion de id
    useEffect(() => {
        async function idPlayer() {
            const response = await fetch(`http://localhost:8080/api/v1/players/nickname/${username}`);
            const data = await response.json();
            setId(data.id);
        }
        if (username){
            idPlayer();
        }
    }, [username]);

    let adminLinks = <></>;
    let playerLinks = <></>;
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
        } else {playerLinks = (
            <NavItem>
                <NavLink style={{ color: "white" }} id="comparator" tag={Link} to="/comparator">Comparador</NavLink>
            </NavItem>
        )}
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
                    <NavLink style={{ color: "white" }} id="achievement" tag={Link} to="/achievements">Achievements</NavLink>
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
                    <texto style={{ color: "#00b318ff", fontWeight: "bold"}}>Inicio</texto>
                </NavbarBrand>
                <NavbarToggler onClick={toggleNavbar} className="ms-2" />
                <Collapse isOpen={!collapsed} navbar>
                    <Nav className="me-auto mb-2 mb-lg-0" navbar>
                        {userLinks}
                        {adminLinks}
                        {playerLinks}
                    </Nav>
                    <Nav className="ms-auto mb-2 mb-lg-0" navbar>
                        {publicLinks}
                        {userLogout}
                    </Nav>
                </Collapse>
            </Navbar>
            {jwt && (
                <FriendsSidebar 
                    isOpen={isOpenFriends} 
                    toggle={toggleMenu} 
                    username={username} 
                    jwt={jwt} 
                    id = { id}
                />
            )}
            
        </div>
    );
}

export default AppNavbar;