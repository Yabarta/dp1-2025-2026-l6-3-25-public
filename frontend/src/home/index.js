import React, { useState } from 'react';
import '../App.css';
import '../static/css/home/home.css'; 
import logo from '../static/images/petris3D_recortado.png'
import Lobby from './Lobby';

export default function Home(){
    const [showMainMenu, setShowMainMenu] = useState(false);
    const [showPrivateRoom, setShowPrivateRoom] = useState(false);
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
        setShowPrivateRoom(true);
        setShowMainMenu(false);
    };

    const handleBackToMenu = () => {
        setShowPrivateRoom(false);
        setShowMainMenu(true);
        setRoomCode('');
    };

    const handleBackToWelcome = () => {
        setShowMainMenu(false);
        setShowPrivateRoom(false);
        setRoomCode('');
    };

    const handleStartGame = () => {
        alert('Iniciar partida - Funcionalidad pendiente');
    };

    if (showPrivateRoom) {
        return (
            <Lobby 
                roomCode={roomCode}
                onBackToMenu={handleBackToMenu}
                onStartGame={handleStartGame}
            />
        );
    }

    if (showMainMenu) {
        return (
            <div className="home-page-container">
                <div className="main-menu">
                    <h1>Petris</h1>
                    <div className="menu-buttons">
                        <button className="menu-button" onClick={() => alert('Buscar partida - Funcionalidad pendiente')}>
                            Buscar Partida
                        </button>
                        <button className="menu-button" onClick={handleCreatePrivateGame}>
                            Crear Partida Privada
                        </button>
                        <button className="menu-button" onClick={() => alert('Ver perfil - Funcionalidad pendiente')}>
                            Ver Perfil
                        </button>
                        <button className="menu-button" onClick={() => alert('Ajustes - Funcionalidad pendiente')}>
                            Ajustes
                        </button>
                        <button className="back-button" onClick={handleBackToWelcome}>
                            Volver
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    return(
        <div className="home-page-container">
            <div className="hero-div">
                <h1>Petris</h1>
                <img src={logo} width={255} height={369} alt=""/>
                <button className="botonAzul" onClick={handlePlayButtonClick}>Do you want to play?</button>                
            </div>
        </div>
    );
}