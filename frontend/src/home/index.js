import React, { useState } from 'react';
import '../static/css/home/home.css';
import GameScreen from '../Game/gameScreen';
import JoinGameScreen from './joinGameScreen';
import logo from '../static/images/petris3D_recortado.png'
import tokenService from 'frontend/src/services/token.service.js';
import ProfileScreen from '../profile/profileScreen';
import {ToastContainer, toast} from 'react-toastify';
import { NavLink, NavItem, Nav, NavbarText, NavbarToggler } from 'reactstrap';
import { Link, useNavigate } from 'react-router-dom';

export default function Home(){
  const navigate = useNavigate();
  const [showMainMenu, setShowMainMenu] = useState(false);
  const [showJoinGameScreen, setShowJoinGameScreen] = useState(false);
  const [showProfile, setShowProfile] = useState(false);
  const jwt = tokenService.getLocalAccessToken();
  const [roomCode, setRoomCode] = useState('');

  const handlePlayButtonClick = () => {
    if (!jwt) {
      navigate('/login');
    } else {
      setShowMainMenu(true);
    }
  };

  const handleBackToMenu = () => {
    setShowJoinGameScreen(false);
    setShowMainMenu(true);
  };

  const handleBackToWelcome = () => {
    setShowMainMenu(false);
    setShowJoinGameScreen(false);
  };
  //Lógica para unirse a una sala existente

  const handleJoinPrivateGame = () => {
    setShowMainMenu(false);
    setShowJoinGameScreen(true);
  };
  
  const handleShowProfile = () => {
    if (jwt == null) {
      return toast.error("User not logged in")
    } else {
      setShowProfile(true)
    }
  }

  if (showJoinGameScreen) {
    return <JoinGameScreen onBackToMenu={handleBackToMenu} />;
  }

  // Lógica para crear una sala con código aleatorio

  const generateRoomCode = () => {
    const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    let code = '';
    for (let i = 0; i < 4; i++) {
      code += letters.charAt(Math.floor(Math.random() * letters.length));
    }
    return code;
  };

  const handleCreatePrivateGame = () => {
    const code = generateRoomCode();
    setRoomCode(code);
    setShowMainMenu(false);
    navigate('/gameScreen', { state: { roomCode: code } });
  };
  if (showProfile) {
    return <ProfileScreen user={jwt} />
  }

  return (
    <div className="homePageContainer">
      <div >
        {showMainMenu ? (
          <div className="mainMenu">
            <h1>Menú Principal</h1>
            <div className="menuButtons">
              <button className="menuButton" onClick={() => toast.error('Funcionalidad pendiente')}>
                Buscar Partida
              </button>
              <button className="menuButton" onClick={handleJoinPrivateGame}>
                Unirse a Partida
              </button>
              <button className="menuButton" onClick={handleCreatePrivateGame}>
                Crear Partida Privada
              </button>
              <button className="menuButton" onClick={handleShowProfile}>
                Ver Perfil
              </button>
              <button className="menuButton" onClick={() => toast.error('Funcionalidad pendiente')}>
                Ajustes
              </button>
            </div>
          </div>
        ) : (
          <div className="heroDiv">
            <h1 style={{ color: '#ffffff' }}>Petris</h1>
            <img src={logo} width={255} height={369} alt=""/>
            <button className="blueButton" onClick={handlePlayButtonClick}>
              ¿Empezamos a Jugar?
            </button>
          </div>
        )}
      </div>
      <ToastContainer />
    </div>
  );
}
