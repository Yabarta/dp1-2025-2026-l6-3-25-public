import React, { useState } from 'react';
import '../static/css/home/home.css';
import GameScreen from '../Game/gameScreen';
import logo from '../static/images/petris3D_recortado.png'

export default function Home(){
  const [showMainMenu, setShowMainMenu] = useState(false);
  const [showGameScreen, setShowGameScreen] = useState(false);
  const [roomCode, setRoomCode] = useState('');

  const handlePlayButtonClick = () => {
    setShowMainMenu(true);
  };

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
    setShowGameScreen(true);
  };

  const handleBackToMenu = () => {
    setShowGameScreen(false);
    setShowMainMenu(true);
  };

  const handleBackToWelcome = () => {
    setShowMainMenu(false);
    setShowGameScreen(false);
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
              <button className="menuButton" onClick={() => alert('Funcionalidad pendiente')}>
                Buscar Partida
              </button>
              <button className="menuButton" onClick={handleCreatePrivateGame}>
                Crear Partida Privada
              </button>
              <button className="menuButton" onClick={() => alert('Funcionalidad pendiente')}>
                Ver Perfil
              </button>
              <button className="menuButton" onClick={() => alert('Funcionalidad pendiente')}>
                Ajustes
              </button>
            </div>
          </div>
        ) : (
          <div className="heroDiv">
            <h1>Petris</h1>
            <img src={logo} width={255} height={369} alt=""/>
            <button className="blueButton" onClick={handlePlayButtonClick}>
              ¿Empezamos a Jugar?
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
