import { useState } from 'react';
import '../static/css/home/home.css';
import JoinGameScreen from './joinGameScreen';
import logo from '../static/images/petris3D_recortado.png'
import tokenService from '../services/token.service.js';
import {ToastContainer, toast} from 'react-toastify';
import { useNavigate } from 'react-router-dom';

const jwt = tokenService.getLocalAccessToken();

export default function Home(){
  const navigate = useNavigate();
  const [showMainMenu, setShowMainMenu] = useState(false);
  const [showJoinGameScreen, setShowJoinGameScreen] = useState(false);
  // profile will be shown via a dedicated route /profile


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

  
  const handleShowProfile = () => {
    if (jwt == null) {
      return toast.error("User not logged in")
    } else {
        navigate('/profile');
    }
  }

  if (showJoinGameScreen) {
    return <JoinGameScreen onBackToMenu={handleBackToMenu} />;
  }

  // Lógica para crear una sala con código aleatorio


  const handleCreatePrivateGame = () => {
    navigate('/lobby');
  };

  const handleDemoGame = () => {  
    setShowMainMenu(false);
    navigate('/demo');
  };



  return (
    <div className="homePageContainer">
      <div >
        {showMainMenu ? (
          <div className="mainMenu">
            <h1>Menú Principal</h1>
            <div className="menuButtonsBox">
              <button className="menuButton" onClick={handleCreatePrivateGame}>
                Jugar
              </button>
              <button className="menuButton" onClick={handleShowProfile}>
                Ver Perfil
              </button>
              <button className="menuButton" onClick={handleDemoGame}>
                Ver Demo
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
