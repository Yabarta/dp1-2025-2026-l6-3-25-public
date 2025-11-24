// Game/Game.jsx
import React, { useState, useEffect, useRef } from "react";
import { initialGameState, turnOrder, nextPhase } from "./demoLogic";
import { useLocation, useNavigate } from 'react-router-dom';
import Board from "./demoBoard";
import '../static/css/game/gameScreen.css';
import ExitModal from '../components/modal/ExitModal';
import ModalWinner from './modalWinner';
import tokenService from '../services/token.service';
import jwt_decode from "jwt-decode";

export default function Game({onBackToMenu}) {
  const [gameState, setGameState] = useState({
    ...initialGameState,
    turnOrder: turnOrder,
  });
  const [username, setUsername] = useState("");
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);

  const [selectedOrigin, setSelectedOrigin] = useState(null);
  const [selectedDest, setSelectedDest] = useState(null);
  const [moveAmount, setMoveAmount] = useState(1);
  const [exitGame, setExitGame] = useState(null);
  const location = useLocation();
  const navigate = useNavigate();

    //contador
  const TIEMPO_INICIAL = 5
  const [timeLeft, setTimeLeft] = useState(TIEMPO_INICIAL)
  const [running, setRunning] = useState(true)
  const intervaloRef = useRef(null)

    const jwt = tokenService.getLocalAccessToken();
      useEffect(() => {
          if (jwt) {
              setUsername(jwt_decode(jwt).sub);
          }
      }, [jwt])

  useEffect(() => {
    if (username) {
      setGameState(prev => {
        const players = Array.isArray(prev.players) ? [...prev.players] : [{ name: 'Jugador 1' }, { name: 'Jugador 2' }];
        players[0] = { ...players[0], name: username };
        return { ...prev, players };
      });
    }
  }, [username]);

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
  
    const handleTimeUp = (values) => {
      if(gameState.players[0] === jwt_decode(jwt).sub && turnOrder[gameState.currentPhaseIndex] == "J1"){
        setGameState(prev => ({ ...prev, winner: gameState.players[1].name }));
      } else if (gameState.players[1] === jwt_decode(jwt).sub && turnOrder[gameState.currentPhaseIndex] == "J2"){
        setGameState(prev => ({ ...prev, winner: gameState.players[0].name }));
      }
    }
  // estilos por jugador (puedes cambiarlos aquí)
  const playerStyles = [
    { color: '#c42323', nameColor: '#c42323' }, // jugador 1: rojo
    { color: '#00dee6ff', nameColor: '#00dee6ff' }, // jugador 2: azul
  ];

  // Componente local: barra de puntuación vertical 0..9
  const ScoreBar = ({ score = 0, color = '#888' }) => {
    const max = 9;
    const clamped = Math.max(0, Math.min(max, Number(score) || 0));
    const fillPercent = (clamped / max) * 100;
    const numbers = Array.from({ length: max + 1 }, (_, i) => max - i); // 9..0

    return (
      <div className="scoreBarContainer" style={{ display: 'flex', alignItems: 'center', gap: 12, paddingLeft: 30, paddingTop: 24 }}>
        <div className="scoreBar" style={{ position: 'relative', width: 56, height: 400, border: '2px solid #000', boxSizing: 'border-box', background: '#fff' }}>
          <div className="scoreFill" style={{ position: 'absolute', left: 0, right: 0, bottom: 0, height: `${fillPercent}%`, background: color, transition: 'height 300ms ease' }} />

          {/* líneas divisorias y etiquetas posicionadas respecto a la barra: centrar en el medio de cada número */}
          {Array.from({ length: max + 1 }).map((_, i) => {
            const percent = (i / max) * 100; // 0..100
            // línea horizontal centrada en el punto
            const line = (
              <div key={`line-${i}`} style={{ position: 'absolute', left: 0, right: 0, top: `${percent}%`, transform: 'translateY(-50%)', height: 0, borderTop: '1px solid rgba(0,0,0,0.25)' }} />
            );
            return line;
          })}

          {/* etiquetas numéricas centradas en las mismas posiciones, a la derecha de la barra */}
          {numbers.map((n, idx) => {
            const percent = (idx / max) * 100;
            return (
              <div key={`label-${n}`} style={{ position: 'absolute', right: -44, top: `${percent}%`, transform: 'translateY(-50%)', fontSize: 14 }}>{n}</div>
            );
          })}
        </div>
      </div>
    );
  };
  

  const currentPhase = gameState.turnOrder[gameState.currentPhaseIndex];

  // Determina el jugador actual (1 o 2)
  const currentPlayer = currentPhase === 'J1' ? 1 : currentPhase === 'J2' ? 2 : null;

  // Handler para seleccionar discos
  const handleDiscoClick = (id) => {
    if (selectedOrigin === null) {
      setSelectedOrigin(id);
      setSelectedDest(null);
    } else if (selectedOrigin !== null && selectedDest === null && id !== selectedOrigin) {
      setSelectedDest(id);
    } else {
      // Si ya hay origen y destino, reinicia selección
      setSelectedOrigin(id);
      setSelectedDest(null);
    }
  };

  // Handler para mover bacterias
  const handleMove = () => {
    if (selectedOrigin === null || selectedDest === null || !currentPlayer) return;
    // Validar cantidad
    const originDisco = gameState.board.find(d => d.id === selectedOrigin);
    const destDisco = gameState.board.find(d => d.id === selectedDest);
    const key = currentPlayer === 1 ? 'j1' : 'j2';
    if (!originDisco || !destDisco || originDisco[key] < moveAmount) return;

    // Actualizar el estado
    const newBoard = gameState.board.map(disco => {
      if (disco.id === selectedOrigin) {
        return { ...disco, [key]: disco[key] - moveAmount };
      } else if (disco.id === selectedDest) {
        return { ...disco, [key]: disco[key] + moveAmount };
      }
      return disco;
    });
    setGameState({ ...gameState, board: newBoard });
    setSelectedOrigin(null);
    setSelectedDest(null);
    setMoveAmount(1);
  };

  const handleEndTurn = async () => {
    setTimeLeft(TIEMPO_INICIAL)
    setRunning(true)
    // If the match has an id, call the backend to compute nextTurn and updated scores.
    if (gameState.id && jwt) {
      try {
        const response = await fetch(`/api/v1/matches/${gameState.id}/nextTurn`, {
          method: 'PUT',
          headers: {
            Authorization: `Bearer ${jwt}`,
            Accept: 'application/json',
            'Content-Type': 'application/json',
          },
          // send the current board state as JSON in the request body (controller expects @RequestBody List<PetriDish>)
          body: JSON.stringify(gameState.board),
        });

        if (response.ok) {
          const updated = await response.json();
          // update local scores and board from the server response
          setGameState((prev) => {
            const p0 = { ...prev.players[0], score: (updated.player1Score ?? prev.players[0].score) };
            const p1 = { ...prev.players[1], score: (updated.player2Score ?? prev.players[1].score) };
            return {
              ...prev,
              board: updated.boardState ? updated.boardState : prev.board,
              players: [p0, p1],
            };
          });
          setGameState((prev) => nextPhase(prev));
        } else {
          console.error('nextTurn failed', response.status);
          setGameState((prev) => nextPhase(prev));
        }
      } catch (err) {
        console.error('Error calling nextTurn', err);
        setGameState((prev) => nextPhase(prev));
      }
    } else {
      //esto es solo para el demo sin backend
      console.log("No hay id de partida, siguiente fase local")
      setGameState((prev) => nextPhase(prev));
    }

    setSelectedOrigin(null);
    setSelectedDest(null);
    setMoveAmount(1);
  };

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

  useEffect(() => {
    if (!gameState.winner) {
      if (gameState.players[0].score >= 9) {
        setGameState(prev => ({ ...prev, winner: gameState.players[1].name }));
      } else if (gameState.players[1].score >= 9) {
        setGameState(prev => ({ ...prev, winner: gameState.players[0].name }));
      }
      if(timeLeft === 0){
        console.log(gameState.players[0].name, jwt_decode(jwt).sub, turnOrder[gameState.currentPhaseIndex])
        if(gameState.players[0].name === jwt_decode(jwt).sub && turnOrder[gameState.currentPhaseIndex] == "J1"){
          setGameState(prev => ({ ...prev, winner: gameState.players[1].name }));
        } else if (gameState.players[1].name === jwt_decode(jwt).sub && turnOrder[gameState.currentPhaseIndex] == "J2"){
          setGameState(prev => ({ ...prev, winner: gameState.players[0].name }));
        }
      }
    }
  }, [gameState.players[0].score, gameState.players[1].score, gameState.winner, timeLeft]);

  const centerColumnStyle = {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem',
    width: '70%',
    boxSizing: 'border-box',
    margin: '0 auto'
  };

  return (
    <div className="gameScreenContainer">
      <div style={{ position: 'relative' }}>
        <div style={{ position: 'absolute', inset: 0 }} />
      </div>
      <ExitModal 
              text='¿Seguro que quieres abandonar la partida?'
              isVisible={exitGame !== null}
              onConfirm={() => {
                handleExit()
              }}
              onCancel={() => {setExitGame(null)}}>
      </ExitModal>

      <ModalWinner winner={gameState.winner} currentUser={username} onGoToMenu={handleBackToMenu} />

      <div className="chatPanel">
        <div className="chatTitle">CHAT</div>
          <div style={{ textAlign: 'center' }}>
            <p style={{ margin: '0.5rem 0', fontSize: '0.9rem' }}>Código de partida:</p>
            {/* <div className="roomCode">{roomCode}</div> */}
            <p>Comparte este código para que se una otro jugador</p>
          </div>
      </div>

      <div style={{ ...centerColumnStyle, background: '#cfeecf',  padding: 12 }}>
        <h1>
          {gameState.winner
            ? `Ganó ${gameState.winner}`
            : `Fase actual: ${currentPhase}`}
        </h1>

        <div style={{ position: 'relative', width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <span className="timer" style={{ position: 'absolute', left: '50%', transform: 'translateX(-50%)' }}>{ timeLeft }</span>

          <button className="back" onClick={() => { setExitGame(true) }}>
            Volver al Menú
          </button>
        </div>

  <div style={{ marginBottom: '0.5rem', minHeight: 20 }}>
          {currentPlayer && selectedOrigin !== null && selectedDest === null && (
            <div style={{ color: '#333' }}>
              Selecciona disco destino para mover bacterias desde <b>{selectedOrigin}</b>.
              <br />
              <label>
                Cantidad a mover:
                <input
                  type="number"
                  min={1}
                  max={4}
                  value={moveAmount}
                  onChange={e => setMoveAmount(Math.max(1, Math.min(4, Number(e.target.value))))}
                  style={{ width: 40, marginLeft: 8 }}
                />
              </label>
            </div>
          )}
          {currentPlayer && selectedOrigin !== null && selectedDest !== null && (
            <div>
              <span style={{ color: '#333' }}>
                Mover <b>{moveAmount}</b> bacterias de <b>{selectedOrigin}</b> a <b>{selectedDest}</b> para J{currentPlayer}
              </span>
              <button
                onClick={handleMove}
                style={{ marginLeft: 12, padding: '0.3rem 1rem', borderRadius: 6, border: 'none', background: '#23c483', color: 'white', fontWeight: 600, cursor: 'pointer' }}
              >
                Confirmar movimiento
              </button>
            </div>
          )}
        </div>

        <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ textAlign: 'center', width: '15vw', minWidth: 120 }}>
            <h2 style={{ color: playerStyles[0].nameColor, margin: 0 }}>{gameState.players[0].name}</h2>
            <ScoreBar score={gameState.players[0].score} color={playerStyles[0].color} />
          </div>

          <div style={{ width: '70vw', maxWidth: '70vw', display: 'flex', justifyContent: 'center' }}>
            <Board
              board={gameState.board}
              playerStyles={playerStyles}
              onDiscoClick={id => {
                if (selectedOrigin === null || (selectedOrigin !== null && selectedDest !== null)) {
                  setSelectedOrigin(id);
                  setSelectedDest(null);
                } else if (selectedOrigin !== null && selectedDest === null && id !== selectedOrigin) {
                  setSelectedDest(id);
                }
              }}
              selectedDisc={selectedDest !== null ? selectedDest : selectedOrigin}
            />
          </div>

          <div style={{ textAlign: 'center', width: '15vw', minWidth: 120 }}>
            <h2 style={{ color: playerStyles[1].nameColor, margin: 0 }}>{gameState.players[1].name}</h2>
            <ScoreBar score={gameState.players[1].score} color={playerStyles[1].color} />
          </div>
        </div>

        <div style={{ display: 'flex', justifyContent: 'center', marginTop: 12 }}>
          <button
            onClick={handleEndTurn}
            style={{
              padding: "1rem 2rem",
              borderRadius: "8px",
              border: "none",
              background: "#78c712ff",
              color: "white",
              cursor: "pointer",
              fontSize: "1.2rem",
              fontFamily: 'Poppins, Arial, sans-serif'
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