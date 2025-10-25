import React, { useEffect, useRef, useState } from 'react';
import '../static/css/game/gameScreen.css';
import ExitModal from '../components/modal/ExitModal';
import { useLocation, useNavigate } from 'react-router-dom';

export default function GameScreen ({ roomCode: propRoomCode, onBackToMenu }) {
  const [waitingForPlayer, setWaitingForPlayer] = useState(true);
  const [exitGame, setExitGame] = useState(null);
  const location = useLocation();
  const navigate = useNavigate();
  //contador
  const TIEMPO_INICIAL = 60
  const [timeLeft, setTimeLeft] = useState(TIEMPO_INICIAL)
  const [running, setRunning] = useState(true)
  const intervaloRef = useRef(null)

  //comprobar que timeLeft no sea 0
  useEffect(() => {
    if(timeLeft == 0){
      handleTimeUp()
    }
  }, [timeLeft])
  // logica del contador
  useEffect(() => {
    if (!running) { 
      clearInterval(intervaloRef.current);
      return;
    }

    // Si 'reset' es true (asumiendo que significa CORRIENDO), establece el intervalo
    intervaloRef.current = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          clearInterval(intervaloRef.current);
          setRunning(false); // Detiene el timer (reset = false)
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(intervaloRef.current);
  }, [running]);

  const handleTimeUp = () => {
    alert("Sin tiempo, has perdido!")
    // aquí iría toda la lógica de si se acaba el tiempo
  }

  const handleContinueTurn = () => {
    setTimeLeft(TIEMPO_INICIAL)
  }

  const roomCode = propRoomCode || (location && location.state && location.state.roomCode) || '';

  const handleBackToMenu = () => {
    if (onBackToMenu) {
      onBackToMenu();
    } else {
      navigate('/');
    }
  }

  const handleExit = () => {
    setExitGame(null)
    handleBackToMenu()
  }


  return (
    <div className="gameScreenContainer">
      <ExitModal 
        text='¿Seguro que quieres abandonar la partida?'
        isVisible={exitGame !== null}
        onConfirm={() => {
          handleExit()
        }}
        onCancel={() => {setExitGame(null)}}>

      </ExitModal>
      <div className="chatPanel">
        <div className="chatTitle">CHAT</div>
        {waitingForPlayer && (
          <div style={{ textAlign: 'center' }}>
            <p style={{ margin: '0.5rem 0', fontSize: '0.9rem' }}>Código de partida:</p>
            <div className="roomCode">{roomCode}</div>
            <p>Comparte este código para que se una otro jugador</p>
          </div>
        )}
      </div>

      <div className="mainPanel">
        <div className="topBar">
          <span className="timer">{ timeLeft }</span>
          {waitingForPlayer && (
            <button className="back" onClick={() => { setExitGame(true) }}>
              Volver al Menú
            </button>
          )}
        </div>
        <div className="playersPanels">
          <div className="playerPanel left">
            <div className="playerInfo">P1</div>
            <div className="playerBar"></div>
            <div className="playerAvatar"></div>
          </div>
          <div className="boardPanel">
            {waitingForPlayer ? (
              <div className="waitingMessage">
                <h2>Esperando al segundo jugador...</h2>
                <div className="loadingSpinner"></div>
                <p>Código: <strong>{roomCode}</strong></p>
              </div>
            ) : (
              <div>Aquí irá el tablero</div>
            )}
          </div>
          <div className="playerPanel right">
            <div className="playerInfo">P2</div>
            <div className="playerBar"></div>
            <div className="playerAvatar"></div>
          </div>
        </div>
        <div className="turnPanel">
          <button className="endTurn" onClick={() => {
            handleContinueTurn()
          }}>
            Terminar turno
          </button>
        </div>
      </div>

      <div className="turnsPanel">
        <div className="turnsTitle">Turnos</div>
        <div className="turnsList">
        </div>
        <div className="turnsCount">1/4</div>
      </div>
    </div>
  );
}