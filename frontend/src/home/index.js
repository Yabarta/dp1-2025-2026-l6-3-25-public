import React, { useState } from 'react';
import '../static/css/home/home.css';
import GameScreen from '../Game/gameScreen';
import JoinGameScreen from './joinGameScreen';
import logo from '../static/images/petris3D_recortado.png'
import {ToastContainer, toast} from 'react-toastify';

export default function Home(){
  const [showMainMenu, setShowMainMenu] = useState(false);
  const [showJoinGameScreen, setShowJoinGameScreen] = useState(false);
  const [showGameScreen, setShowGameScreen] = useState(false);
  const [roomCode, setRoomCode] = useState('');

  const handlePlayButtonClick = () => {
    setShowMainMenu(true);
  };

  const handleBackToMenu = () => {
    setShowGameScreen(false);
    setShowJoinGameScreen(false);
    setShowMainMenu(true);
  };

  const handleBackToWelcome = () => {
    setShowMainMenu(false);
    setShowJoinGameScreen(false);
    setShowGameScreen(false);
  };
  //Lógica para unirse a una sala existente

  const handleJoinPrivateGame = () => {
    setShowMainMenu(false);
    setShowJoinGameScreen(true);
    setShowGameScreen(false);
  };

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
    setShowJoinGameScreen(false);
    setShowGameScreen(true);
  };

  if (showGameScreen) {
    return <GameScreen roomCode={roomCode} onBackToMenu={handleBackToMenu} />;
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
              <button className="menuButton" onClick={() => toast.error('Funcionalidad pendiente')}>
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
