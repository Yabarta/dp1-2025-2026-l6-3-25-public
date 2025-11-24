import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { matchPath, useNavigate, useParams } from 'react-router-dom';
import '../static/css/game/gameScreen.css';
import ExitGameModal from '../components/modal/ExitGameModal';
import useWebSocket from '../hooks/useWebSocket';
import api from '../services/api';
import tokenService from '../services/token.service';
import Board from './demoBoard';
import Chat from './Chat/Chat';

function ScoreBar({ score = 0, color = '#888' }) {
  const max = 9;
  const clamped = Math.max(0, Math.min(max, Number(score) || 0));
  const fillPercent = (clamped / max) * 100;
  const ticks = Array.from({ length: max + 1 }, (_, i) => i);

  return (
    <div className="scoreBarContainer">
      <div className="scoreBarFrame">
        <div className="scoreBarFill" style={{ height: `${fillPercent}%`, background: `linear-gradient(180deg, ${color} 0%, rgba(12, 24, 15, 0.9) 100%)` }} />
        {ticks.map((value) => {
          const percent = 100 - (value / max) * 100;
          return <span key={`line-${value}`} className="scoreBarTick" style={{ top: `${percent}%` }} />;
        })}
        {ticks.map((value) => {
          const percent = 100 - (value / max) * 100;
          return (
            <span key={`label-${value}`} className="scoreBarLabel" style={{ top: `${percent}%` }}>
              {value}
            </span>
          );
        })}
      </div>
    </div>
  );
}
const TURN_TIME_SECONDS = 60;
const MAX_BACTERIA = 5;
const PETRI_ADJACENCIES = {
  0: [1, 2, 3],
  1: [0, 3, 4],
  2: [0, 3, 5],
  3: [0, 1, 2, 4, 5, 6],
  4: [1, 3, 6],
  5: [2, 3, 6],
  6: [3, 4, 5],
};

const TURN_SEQUENCE = [
  'P1_PROPAGATION',
  'P2_PROPAGATION',
  'BINARY_FISSION',
  'P2_PROPAGATION',
  'P1_PROPAGATION',
  'BINARY_FISSION',
  'P1_PROPAGATION',
  'P2_PROPAGATION',
  'BINARY_FISSION',
  'CONTAMINATION',
  'P2_PROPAGATION',
  'P1_PROPAGATION',
  'BINARY_FISSION',
  'P1_PROPAGATION',
  'P2_PROPAGATION',
  'BINARY_FISSION',
  'P2_PROPAGATION',
  'P1_PROPAGATION',
  'BINARY_FISSION',
  'CONTAMINATION',
  'P1_PROPAGATION',
  'P2_PROPAGATION',
  'BINARY_FISSION',
  'P2_PROPAGATION',
  'P1_PROPAGATION',
  'BINARY_FISSION',
  'P1_PROPAGATION',
  'P2_PROPAGATION',
  'BINARY_FISSION',
  'CONTAMINATION',
  'P2_PROPAGATION',
  'P1_PROPAGATION',
  'BINARY_FISSION',
  'P1_PROPAGATION',
  'P2_PROPAGATION',
  'BINARY_FISSION',
  'P2_PROPAGATION',
  'P1_PROPAGATION',
  'BINARY_FISSION',
  'CONTAMINATION',
];

const ROUND_SIZE = 10;
const TURN_ROUNDS = Array.from({ length: Math.ceil(TURN_SEQUENCE.length / ROUND_SIZE) }, (_, roundIndex) =>
  TURN_SEQUENCE.slice(roundIndex * ROUND_SIZE, (roundIndex + 1) * ROUND_SIZE),
);

const TURN_PHASE_META = {
  P1_PROPAGATION: { label: 'P1', description: 'Propagación jugador 1', className: 'turnPhase--p1' },
  P2_PROPAGATION: { label: 'P2', description: 'Propagación jugador 2', className: 'turnPhase--p2' },
  BINARY_FISSION: { label: 'F', description: 'Fisión binaria', className: 'turnPhase--binary' },
  CONTAMINATION: { label: 'C', description: 'Contaminación', className: 'turnPhase--contamination' },
};

const QUICK_AMOUNT_OPTIONS = [1, 2, 3, 4, 5];


export default function GameScreen() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [match, setMatch] = useState(null);
  const [error, setError] = useState(null);
  const [exitGame, setExitGame] = useState(false);
  const [timeLeft, setTimeLeft] = useState(TURN_TIME_SECONDS);
  const [running, setRunning] = useState(false);
  const [isEndingTurn, setIsEndingTurn] = useState(false);
  const [editedBoard, setEditedBoard] = useState([]);
  const [selectedSource, setSelectedSource] = useState(null);
  const [moveAmount, setMoveAmount] = useState(1);
  const [selectedTarget, setSelectedTarget] = useState(null);
  const [selectionLocked, setSelectionLocked] = useState(false);
  const [boardFeedback, setBoardFeedback] = useState(null);
  const [currentUser] = useState(() => tokenService.getUser());
  const [lastMove, setLastMove] = useState({ source: null, target: null });
  const timerRef = useRef(null);
  const lastTurnRef = useRef(null);
  const previousBoardRef = useRef([]);


  const matchUpdate = useWebSocket(`/app/matches/watch/${id}`, `/topic/match/${id}`);

function PlayerColumn({ player, fallbackLabel, score, style }) {
  const displayName = player?.nickname ?? player?.username ?? fallbackLabel;
  const safeScore = score ?? 0;

  return (
    <div className="playerColumn">
      <div className="playerName" style={{ color: style.nameColor }}>
        {displayName}
      </div>
      <ScoreBar score={safeScore} color={style.color} />
    </div>
  );
}

function BoardStage({ waitingForPlayer, roomCode, boardProps, controlsProps }) {
  if (waitingForPlayer) {
    return (
      <div className="waitingStage">
        <h2>Esperando al segundo jugador...</h2>
        <div className="loadingSpinner" />
        <p>
          Código: <strong>{roomCode}</strong>
        </p>
      </div>
    );
  }

  return (
    <>
      <div className="boardWrapper">
        <Board {...boardProps} />
      </div>
      <BoardControls {...controlsProps} />
    </>
  );
}

function BoardControls({
  boardInstruction,
  canEditBoard,
  moveAmountValue,
  maxMoveForSource,
  onMoveAmountChange,
  quickAmountOptions,
  moveAmount,
  selectedSource,
  sourceCapacity,
  onQuickAmountClick,
  onApplyMove,
  applyDisabled,
  onCancelSelection,
  cancelDisabled,
  boardFeedback,
  lastMoveText,
}) {
  return (
    <div className="boardControls">
      <p className="boardInstruction">{boardInstruction}</p>
      {canEditBoard && (
        <>
          <div className="controlRow">
            <label htmlFor="move-amount">Unidades a mover:</label>
            <input
              id="move-amount"
              type="number"
              min="1"
              max={maxMoveForSource}
              value={moveAmountValue}
              onChange={onMoveAmountChange}
            />
            <div className="quick-amounts">
              {quickAmountOptions.map((value) => (
                <button
                  key={value}
                  type="button"
                  className={moveAmount === value ? 'active' : ''}
                  disabled={selectedSource === null || value > sourceCapacity}
                  onClick={() => onQuickAmountClick(value)}
                >
                  {value}
                </button>
              ))}
            </div>
          </div>
          <div className="controlRow controlRow--actions">
            <button type="button" onClick={onApplyMove} disabled={applyDisabled}>
              Aplicar movimiento
            </button>
            <button type="button" onClick={onCancelSelection} disabled={cancelDisabled}>
              Cancelar selección
            </button>
          </div>
        </>
      )}
      {boardFeedback && <div className="board-feedback">{boardFeedback}</div>}
      <div className="last-move-info">
        Último movimiento: <strong>{lastMoveText}</strong>
      </div>
    </div>
  );
}

function TurnTimeline({
  currentPhaseMeta,
  activeRoundIndex,
  activeRoundPhases,
  currentTurnIndex,
  turnTrackOffset,
  turnTrackRef,
}) {
  const totalTurns = TURN_SEQUENCE.length;
  const safeDisplayTurn = currentTurnIndex < 0 ? 0 : Math.min(currentTurnIndex, totalTurns - 1);
  const turnNumberLabel = currentTurnIndex < 0 ? 0 : safeDisplayTurn + 1;
  return (
    <>
      <div className="turnSummary">Turno {turnNumberLabel%10} · Ronda {activeRoundIndex + 1}</div>
      {currentPhaseMeta && <small>{currentPhaseMeta.description}</small>}
      <div className="turnsList">
        <div className="turnTimeline">
          <div className="turnRound" key={`round-${activeRoundIndex}`}>
            
            <div
              className="turnRoundTrack"
              ref={turnTrackRef}
              style={{ transform: `translateY(${turnTrackOffset}px)` }}
            >
              {activeRoundPhases.map((phase, phaseIdx) => {
                const globalIndex = activeRoundIndex * ROUND_SIZE + phaseIdx;
                const meta = TURN_PHASE_META[phase] || { label: '?', description: phase, className: 'turnPhase--default' };
                const circleClasses = ['turnPhase', meta.className];
                const wrapperClasses = ['turnPhaseItem'];
                if (currentTurnIndex > globalIndex) {
                  circleClasses.push('is-complete');
                  wrapperClasses.push('is-complete');
                } else if (currentTurnIndex === globalIndex) {
                  circleClasses.push('is-current');
                  wrapperClasses.push('is-current');
                }
                const showConnector = phaseIdx < activeRoundPhases.length - 1;
                return (
                  <div
                    key={`phase-${globalIndex}`}
                    className={wrapperClasses.join(' ')}
                    title={`Ronda ${activeRoundIndex + 1} · ${meta.description}`}
                  >
                    <div className={circleClasses.join(' ')}>
                      <span className="turnPhaseLabel">
                        {meta.label}
                        <small>{globalIndex + 1}</small>
                      </span>
                    </div>
                    {showConnector && <span className="turnPhaseConnector" />}
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

function useTurnTracker(activeRoundIndex, currentPhaseIndexInRound, currentTurnIndex) {
  const turnTrackRef = useRef(null);
  const previousRoundRef = useRef(null);
  const previousPhaseIndexRef = useRef(null);
  const [turnTrackOffset, setTurnTrackOffset] = useState(0);

  useEffect(() => {
    const trackElement = turnTrackRef.current;
    if (!trackElement) {
      previousRoundRef.current = activeRoundIndex;
      previousPhaseIndexRef.current = currentPhaseIndexInRound;
      setTurnTrackOffset(0);
      return;
    }

    const phaseNodes = trackElement.querySelectorAll('.turnPhaseItem');
    if (!phaseNodes.length) {
      previousRoundRef.current = activeRoundIndex;
      previousPhaseIndexRef.current = currentPhaseIndexInRound;
      setTurnTrackOffset(0);
      return;
    }

    const safeIndex = Math.min(Math.max(currentPhaseIndexInRound, 0), phaseNodes.length - 1);
    const roundChanged = previousRoundRef.current !== null && previousRoundRef.current !== activeRoundIndex;
    const waitingForTurn = currentTurnIndex < 0;

    if (roundChanged || waitingForTurn) {
      previousRoundRef.current = activeRoundIndex;
      previousPhaseIndexRef.current = safeIndex;
      setTurnTrackOffset(0);
      return;
    }

    if (previousPhaseIndexRef.current === null) {
      previousPhaseIndexRef.current = safeIndex;
      previousRoundRef.current = activeRoundIndex;
      setTurnTrackOffset(0);
      return;
    }

    const previousIndex = previousPhaseIndexRef.current;
    previousRoundRef.current = activeRoundIndex;

    if (previousIndex === safeIndex) {
      return;
    }

    const currentNode = phaseNodes[safeIndex];
    const previousNode = phaseNodes[previousIndex];
    previousPhaseIndexRef.current = safeIndex;

    if (!currentNode || !previousNode) {
      return;
    }

    const delta = currentNode.offsetTop - previousNode.offsetTop;
    if (!Number.isFinite(delta) || delta === 0) {
      return;
    }

    setTurnTrackOffset((prevOffset) => prevOffset - delta);
  }, [activeRoundIndex, currentPhaseIndexInRound, currentTurnIndex]);

  return { turnTrackRef, turnTrackOffset };
}

  useEffect(() => {
    const fetchMatch = async () => {
      try {
        const response = await api.get(`/api/v1/matches/${id}`);
        setError(null);
        setMatch(normaliseMatch(response.data));
      } catch (err) {
        console.error('Unable to load match', err);
        setError('No se pudo cargar la partida.');
        navigate('/lobby', { replace: true });
      }
    };

    fetchMatch();
  }, [id, navigate]);

  useEffect(() => {
    if (!matchUpdate) {
      return;
    }
    if (typeof matchUpdate === 'string') {
      return;
    }
    setError(null);
    setMatch(normaliseMatch(matchUpdate));
  }, [matchUpdate]);

  useEffect(() => {
    const currentBoard = match?.board ? match.board.map((dish) => ({ ...dish })) : [];
    const previousBoard = previousBoardRef.current;
    let derivedLastMove = { source: null, target: null };
    if (previousBoard.length === currentBoard.length && currentBoard.length > 0) {
      const diff = currentBoard.map((dish, idx) => {
        const prevDish = previousBoard[idx];
        const deltaP1 = dish.player1Bacteria - prevDish.player1Bacteria;
        const deltaP2 = dish.player2Bacteria - prevDish.player2Bacteria;
        return { deltaP1, deltaP2 };
      });
      let activeKey = null;
      for (const entry of diff) {
        if (entry.deltaP1 !== 0) {
          activeKey = 'deltaP1';
          break;
        }
        if (entry.deltaP2 !== 0) {
          activeKey = 'deltaP2';
          break;
        }
      }
      if (activeKey) {
        const sourceIndex = diff.findIndex((entry) => entry[activeKey] < 0);
        const targetIndex = diff.findIndex((entry) => entry[activeKey] > 0);
        derivedLastMove = {
          source: sourceIndex >= 0 ? sourceIndex : null,
          target: targetIndex >= 0 ? targetIndex : null,
        };
      }
    }
    setEditedBoard(currentBoard);
    setSelectedSource(null);
    setSelectedTarget(null);
    setMoveAmount(1);
    setSelectionLocked(false);
    setBoardFeedback(null);
    setLastMove(derivedLastMove);
    previousBoardRef.current = currentBoard.map((dish) => ({ ...dish }));
  }, [match?.board]);

  useEffect(() => {
    if (!match || !match.startedAt) {
      setRunning(false);
      setTimeLeft(TURN_TIME_SECONDS);
      return;
    }
    setRunning(true);
  }, [match]);

  useEffect(() => {
    if (!match || match.endedAt) {
      setRunning(false);
    }
  }, [match]);

  useEffect(() => {
    const started = Boolean(match?.startedAt);
    const ended = Boolean(match?.endedAt);
    const currentTurn = match?.turn ?? null;
    if (!started || ended) {
      lastTurnRef.current = null;
      return;
    }
    if (lastTurnRef.current === null) {
      lastTurnRef.current = currentTurn;
      return;
    }
    if (lastTurnRef.current !== currentTurn) {
      setTimeLeft(TURN_TIME_SECONDS);
      setRunning(true);
    }
    lastTurnRef.current = currentTurn;
  }, [match?.turn, match?.startedAt, match?.endedAt]);

  useEffect(() => {
    if (!running) {
      clearInterval(timerRef.current);
      return;
    }

    timerRef.current = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          clearInterval(timerRef.current);
          setRunning(false);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timerRef.current);
  }, [running]);

  useEffect(() => {
    if (timeLeft === 0) {
      handleTimeUp();
    }
  }, [timeLeft]);

  const isPlayer1 = Boolean(currentUser && match?.player1?.username === currentUser.username);
  const isPlayer2 = Boolean(currentUser && match?.player2?.username === currentUser.username);
  let nickname = currentUser?.username || 'Invitado'; // Valor por defecto (si eres espectador)

  if (match) {
    if (isPlayer1) {
      // Si soy el P1, uso el nickname del P1 (o su username si no tiene nick)
      nickname = match.player1?.nickname ?? match.player1?.username ?? 'Player 1';
    } else if (isPlayer2) {
      // Si soy el P2, uso el nickname del P2
      nickname = match.player2?.nickname ?? match.player2?.username ?? 'Player 2';
    }
  }
  
  const iAmParticipant = isPlayer1 || isPlayer2;
  const hasMatch = Boolean(match);
  const matchEnded = Boolean(match?.endedAt);
  const currentPlayerKey = isPlayer1 ? 'player1Bacteria' : isPlayer2 ? 'player2Bacteria' : 'player1Bacteria';
  const opponentPlayerKey = currentPlayerKey === 'player1Bacteria' ? 'player2Bacteria' : 'player1Bacteria';
  const isPropagationTurn = match?.turnType === 'P1_PROPAGATION' || match?.turnType === 'P2_PROPAGATION';
  const isMyPropagationTurn = (match?.turnType === 'P1_PROPAGATION' && isPlayer1) || (match?.turnType === 'P2_PROPAGATION' && isPlayer2);
  const canEditBoard = Boolean(isMyPropagationTurn && !match?.endedAt);
  const waitingForPlayer = useMemo(() => !match || !match.player2, [match]);
  const roomCode = match?.code ?? '';
  const boardChanged = useMemo(() => {
    if (!match?.board) {
      return editedBoard.length > 0;
    }
    if (editedBoard.length !== match.board.length) {
      return true;
    }
    return editedBoard.some((dish, idx) => {
      const serverDish = match.board[idx];
      if (!serverDish) {
        return true;
      }
      return dish.player1Bacteria !== serverDish.player1Bacteria || dish.player2Bacteria !== serverDish.player2Bacteria;
    });
  }, [editedBoard, match]);
  const isMyTurn = Boolean(match && !matchEnded && (isPropagationTurn ? isMyPropagationTurn : iAmParticipant));
  const totalTurnPhases = TURN_SEQUENCE.length;
  const rawTurnIndex = typeof match?.turn === 'number' ? match.turn : -1;
  const consumedAllPhases = rawTurnIndex >= totalTurnPhases;
  const clampedTurnIndex = rawTurnIndex >= 0
    ? Math.min(rawTurnIndex, totalTurnPhases - 1)
    : rawTurnIndex;
  const timelineTurnIndex = consumedAllPhases ? totalTurnPhases : clampedTurnIndex;
  const currentPhaseMeta = match?.turnType ? TURN_PHASE_META[match.turnType] : null;
  const activeRoundIndex = clampedTurnIndex >= 0
    ? Math.min(Math.floor(clampedTurnIndex / ROUND_SIZE), TURN_ROUNDS.length - 1)
    : 0;
  const activeRoundPhases = TURN_ROUNDS[activeRoundIndex] ?? TURN_ROUNDS[0];
  const currentPhaseIndexInRound = clampedTurnIndex >= 0
    ? clampedTurnIndex - activeRoundIndex * ROUND_SIZE
    : 0;
  const { turnTrackRef, turnTrackOffset } = useTurnTracker(activeRoundIndex, currentPhaseIndexInRound, timelineTurnIndex);

  const handleTimeUp = () => {
    try {
      api.put(`/api/v1/matches/${id}/endMatch`);
    } catch (err) {
      console.error('Unable to end match cleanly', err);
    } 
  };

  const handleBackToMenu = () => {
    navigate('/lobby');
  };

  const handleExit = async () => {
    setExitGame(false);
    try {
      if (match?.startedAt && !match?.endedAt) {
        await api.put(`/api/v1/matches/${id}/endMatch`);
      } else if (match && !match.startedAt) {
        await api.put(`/api/v1/matches/${id}/leave`);
      }
    } catch (err) {
      console.error('Unable to close match cleanly', err);
      setError('No se pudo cerrar la partida correctamente.');
    } finally {
      handleBackToMenu();
    }
  };

  const handleEndTurn = async () => {
    if (!match || !isMyTurn) {
      return;
    }
    const propagationTurn = isPropagationTurn;
    if (propagationTurn && !canEditBoard) {
      setBoardFeedback('Esperando a que el rival complete su turno.');
      return;
    }
    if (propagationTurn && !boardChanged) {
      setBoardFeedback('Realiza un movimiento antes de terminar el turno.');
      return;
    }
    setIsEndingTurn(true);
    setError(null);
    if (propagationTurn) {
      setBoardFeedback(null);
    }
    try {
      let response;
      if (propagationTurn) {
        const payload = editedBoard.map(({ player1Bacteria, player2Bacteria }) => ({
          player1Bacteria,
          player2Bacteria,
        }));
        response = await api.put(`/api/v1/matches/${id}/nextTurn`, payload);
      } else {
        response = await api.put(`/api/v1/matches/${id}/nextTurn`);
      }
      setMatch(normaliseMatch(response.data));
      setTimeLeft(TURN_TIME_SECONDS);
      setRunning(true);
      if (propagationTurn && selectedSource !== null) {
        setLastMove({ source: selectedSource, target: null });
      }
    } catch (err) {
      console.error('Unable to advance turn', err);
      const serverMessage = err.response?.data?.message ?? err.response?.data ?? err.message;
      if (propagationTurn) {
        const text = Array.isArray(serverMessage) ? serverMessage.join(' ') : String(serverMessage);
        setBoardFeedback(text || 'No se pudo avanzar al siguiente turno.');
      } else {
        setError('No se pudo avanzar al siguiente turno.');
      }
    } finally {
      setIsEndingTurn(false);
      setSelectionLocked(false);
    }
  };

  const canSelectSource = (index) => {
    if (!canEditBoard) {
      return false;
    }
    const dish = editedBoard[index];
    return Boolean(dish && dish[currentPlayerKey] > 0);
  };

  const canMoveTo = (targetIndex) => {
    if (!canEditBoard || selectedSource === null || selectedSource === targetIndex) {
      return false;
    }
    if (!PETRI_ADJACENCIES[selectedSource]?.includes(targetIndex)) {
      return false;
    }
    const sourceDish = editedBoard[selectedSource];
    const targetDish = editedBoard[targetIndex];
    if (!sourceDish || !targetDish) {
      return false;
    }
    const amount = Math.min(moveAmount, sourceDish[currentPlayerKey]);
    if (amount <= 0 || amount >= 5) {
      return false;
    }
    if (targetDish[currentPlayerKey] + amount > MAX_BACTERIA) {
      return false;
    }
    if ((targetDish[currentPlayerKey] + amount) === targetDish[opponentPlayerKey]) {
      return false;
    }
    if (sourceDish[opponentPlayerKey] !== 0 && targetDish[opponentPlayerKey] === amount) {
      return false;
    }
    if (sourceDish[opponentPlayerKey] !== 0 && sourceDish[currentPlayerKey] - amount === sourceDish[opponentPlayerKey]) {
      return false;
    }
    return true;
  };

  const handleMoveTo = (targetIndex) => {
    if (!canMoveTo(targetIndex)) {
      setBoardFeedback('El movimiento no es válido para ese destino.');
      return;
    }
    const sourceDish = editedBoard[selectedSource];
    const amount = Math.min(moveAmount, sourceDish[currentPlayerKey]);
    const updatedBoard = editedBoard.map((dish, idx) => {
      if (idx === selectedSource) {
        return { ...dish, [currentPlayerKey]: dish[currentPlayerKey] - amount };
      }
      if (idx === targetIndex) {
        return { ...dish, [currentPlayerKey]: dish[currentPlayerKey] + amount };
      }
      return dish;
    });
    setEditedBoard(updatedBoard);
    setSelectedTarget(null);
    const remaining = updatedBoard[selectedSource][currentPlayerKey];
    if (remaining > 0) {
      setMoveAmount(Math.min(moveAmount, remaining));
      setBoardFeedback('Movimiento aplicado. Puedes seguir repartiendo bacterias desde la misma placa.');
      setLastMove({ source: selectedSource, target: targetIndex });
      setSelectionLocked(true);
    } else {
      setMoveAmount(1);
      setBoardFeedback('Movimiento aplicado. No quedan bacterias en la placa origen. Pulsa "Terminar turno" para finalizar tu turno.');
      setSelectionLocked(true);
      setLastMove({ source: selectedSource, target: targetIndex });
    }
  };

  const handleBoardClick = (plateId) => {
    if (!canEditBoard) {
      return;
    }
    const index = editedBoard.findIndex((dish, idx) => (dish.index ?? idx) === plateId);
    if (index < 0) {
      return;
    }
    if (selectedSource === null) {
      if (canSelectSource(index)) {
        setSelectedSource(index);
        setSelectedTarget(null);
        setMoveAmount(1);
        setBoardFeedback(null);
      } else {
        setBoardFeedback('Selecciona una placa con tus bacterias.');
      }
      return;
    }
    if (selectedSource === index) {
      if (!selectionLocked) {
        setSelectedSource(null);
        setSelectedTarget(null);
        setMoveAmount(1);
        setBoardFeedback(null);
      }
      return;
    }
    if (!canMoveTo(index)) {
      setSelectedTarget(null);
      setBoardFeedback('El movimiento no es válido para ese destino.');
      return;
    }
    setSelectedTarget(index);
    setBoardFeedback('Destino listo. Pulsa "Aplicar movimiento" para confirmar.');
  };

  const handleConfirmMove = () => {
    if (selectedSource === null) {
      setBoardFeedback('Selecciona un plato de origen.');
      return;
    }
    if (selectedTarget === null) {
      setBoardFeedback('Selecciona un destino adyacente válido.');
      return;
    }
    if (!canMoveTo(selectedTarget)) {
      setBoardFeedback('El movimiento ya no es válido para ese destino.');
      setSelectedTarget(null);
      return;
    }
    handleMoveTo(selectedTarget);
  };

  const handleMoveAmountChange = (event) => {
    const rawValue = Number(event.target.value);
    if (Number.isNaN(rawValue)) {
      return;
    }
    if (selectedSource === null) {
      setBoardFeedback('Selecciona primero una placa de origen.');
      return;
    }
    const maxAllowed = Math.max(1, sourceCapacity || 1);
    const clamped = Math.min(Math.max(1, rawValue), maxAllowed);
    setMoveAmount(clamped);
  };

  const boardInstruction = useMemo(() => {
    if (waitingForPlayer) {
      return 'Esperando a que llegue el segundo jugador.';
    }
    if (!hasMatch) {
      return 'Cargando partida...';
    }
    if (matchEnded) {
      return 'La partida ha terminado.';
    }
    if (!isPropagationTurn) {
      return 'Esta fase no requiere edición manual del tablero.';
    }
    if (!iAmParticipant) {
      return 'Observando la partida.';
    }
    if (!isMyPropagationTurn) {
      return 'Esperando a que el rival realice su movimiento.';
    }
    if (selectedSource === null) {
      return 'Selecciona un plato de origen con tus bacterias.';
    }
    if (selectedTarget === null) {
      return 'Elige un destino adyacente y ajusta la cantidad a mover.';
    }
    return 'Pulsa "Aplicar movimiento" para confirmar el traslado.';
  }, [waitingForPlayer, hasMatch, matchEnded, isPropagationTurn, iAmParticipant, isMyPropagationTurn, selectedSource, selectedTarget]);

  const plateIdAtIndex = useCallback((plateIndex) => {
    if (plateIndex === null || plateIndex === undefined || plateIndex < 0) {
      return null;
    }
    const dish = editedBoard[plateIndex];
    return dish ? dish.index ?? plateIndex : null;
  }, [editedBoard]);

  const plateLabel = useCallback((plateIndex) => {
    if (plateIndex === null || plateIndex === undefined || plateIndex < 0) {
      return null;
    }
    const dish = editedBoard[plateIndex];
    const visualIndex = dish ? (dish.index ?? plateIndex) + 1 : plateIndex + 1;
    return `Placa ${visualIndex}`;
  }, [editedBoard]);

  const lastMoveText = useMemo(() => {
    if (lastMove.source === null && lastMove.target === null) {
      return 'Aún no hay jugadas registradas.';
    }
    const sourceLabel = plateLabel(lastMove.source);
    const targetLabel = plateLabel(lastMove.target);
    if (sourceLabel && targetLabel) {
      return `${sourceLabel} → ${targetLabel}`;
    }
    return sourceLabel ?? targetLabel ?? 'Movimiento no disponible.';
  }, [lastMove, plateLabel]);

  const playerStyles = useMemo(() => ([
    { color: '#c42323', nameColor: '#c42323' },
    { color: '#00dee6', nameColor: '#00dee6' },
  ]), []);

  const hexBoard = useMemo(() => editedBoard.map((dish, idx) => ({
    id: dish?.index ?? idx,
    j1: dish?.player1Bacteria ?? 0,
    j2: dish?.player2Bacteria ?? 0,
  })), [editedBoard]);

  const selectedSourceId = plateIdAtIndex(selectedSource);
  const selectedTargetId = plateIdAtIndex(selectedTarget);
  const lastMoveIds = useMemo(() => ({
    source: plateIdAtIndex(lastMove.source),
    target: plateIdAtIndex(lastMove.target),
  }), [lastMove, plateIdAtIndex]);
  const sourceCapacity = selectedSource !== null && editedBoard[selectedSource]
    ? editedBoard[selectedSource][currentPlayerKey]
    : 0;
  const maxMoveForSource = Math.max(1, sourceCapacity || 1);
  const moveAmountValue = Math.min(moveAmount, maxMoveForSource);
  const canApplyMove = canEditBoard && selectedSource !== null && selectedTarget !== null;
  const canCancelSelection = canEditBoard && (selectedSource !== null || selectedTarget !== null) && !selectionLocked;

  const handleQuickAmountSelect = useCallback((value) => {
    setMoveAmount(Math.min(value, maxMoveForSource));
  }, [maxMoveForSource]);

  const handleCancelSelection = useCallback(() => {
    setSelectedSource(null);
    setSelectedTarget(null);
    setMoveAmount(1);
    setBoardFeedback(null);
    setSelectionLocked(false);
  }, []);

  useEffect(() => {
    if (selectedSource === null) {
      if (moveAmount !== 1) {
        setMoveAmount(1);
      }
      return;
    }
    const limit = Math.max(1, sourceCapacity || 1);
    if (moveAmount > limit) {
      setMoveAmount(limit);
    }
  }, [selectedSource, sourceCapacity, moveAmount]);

  return (
    <div className="gameScreenContainer">
      <ExitGameModal
        text="¿Seguro que quieres abandonar la partida?"
        isVisible={exitGame}
        onConfirm={handleExit}
        onCancel={() => setExitGame(false)}
      />

      <aside className="chatPanel">
        <span className="">Tiempo Restante: {timeLeft} s</span>
        <div className="chatTitle">CHAT</div>
        <div className="chatList">
          <Chat nickname={nickname}/>
        </div>
        {waitingForPlayer && (
          <div className="chatRoomInfo">
            <p>Código de partida:</p>
            <div className="roomCode">{roomCode}</div>
            <p>Comparte este código para que se una otro jugador</p>
          </div>
        )}
      </aside>

      <main className="gameMainPanel">
        <div className={`gameStage ${waitingForPlayer ? 'gameStage--waiting' : ''}`}>
          <PlayerColumn
            player={match?.player1}
            fallbackLabel="P1"
            score={match?.player1Score ?? 0}
            style={playerStyles[0]}
          />

          <div className="boardStage">
            <BoardStage
              waitingForPlayer={waitingForPlayer}
              roomCode={roomCode}
              boardProps={{
                board: hexBoard,
                onDiscoClick: handleBoardClick,
                selectedSource: selectedSourceId,
                selectedTarget: selectedTargetId,
                lastMove: lastMoveIds,
                disabled: !canEditBoard,
                playerStyles,
              }}
              controlsProps={{
                boardInstruction,
                canEditBoard,
                moveAmountValue,
                maxMoveForSource,
                onMoveAmountChange: handleMoveAmountChange,
                quickAmountOptions: QUICK_AMOUNT_OPTIONS,
                moveAmount,
                selectedSource,
                sourceCapacity,
                onQuickAmountClick: handleQuickAmountSelect,
                onApplyMove: handleConfirmMove,
                applyDisabled: !canApplyMove,
                onCancelSelection: handleCancelSelection,
                cancelDisabled: !canCancelSelection,
                boardFeedback,
                lastMoveText,
              }}
            />
          </div>

          <PlayerColumn
            player={match?.player2}
            fallbackLabel="P2"
            score={match?.player2Score ?? 0}
            style={playerStyles[1]}
          />
        </div>

        {match?.endedAt && (
          <div className="game-result">
            <strong>Partida finalizada.</strong>{' '}
            {match.winner
              ? `Ganador: ${match.winner === 1 ? (match.player1?.nickname ?? match.player1?.username ?? 'Jugador 1') : (match.player2?.nickname ?? match.player2?.username ?? 'Jugador 2')}`
              : 'Empate.'}
          </div>
        )}
        {error && <div className="error-banner">{error}</div>}
      </main>

      <aside className="turnsPanel">
        <button className="back" onClick={() => setExitGame(true)}>
            Volver al Menú
          </button>
        <TurnTimeline
          currentPhaseMeta={currentPhaseMeta}
          activeRoundIndex={activeRoundIndex}
          activeRoundPhases={activeRoundPhases}
          currentTurnIndex={timelineTurnIndex}
          turnTrackOffset={turnTrackOffset}
          turnTrackRef={turnTrackRef}
        />
          {match ? (
            
              <button
            className="endTurn"
            onClick={handleEndTurn}
            disabled={waitingForPlayer || isEndingTurn || !match || match.endedAt || !isMyTurn}
            title={!isMyTurn ? 'Esperando al otro jugador' : undefined}
          >
            {isEndingTurn ? 'Enviando...' : 'Terminar turno'}
          </button>
          ) : (
            '—'
          )}
      </aside>
    </div>
  );
}
function normaliseMatch(rawMatch) {
  if (!rawMatch) {
    return null;
  }

  const playerSummary = (player) => {
    if (!player) {
      return null;
    }
    return {
      id: player.id,
      nickname: player.nickname,
      username: player.user?.username ?? player.username ?? null,
    };
  };

  const board = Array.isArray(rawMatch.board)
    ? rawMatch.board.map((dish) => ({
        index: dish.index,
        player1Bacteria: dish.player1Bacteria,
        player2Bacteria: dish.player2Bacteria,
      }))
    : Array.isArray(rawMatch.boardState)
      ? rawMatch.boardState.map((dish, index) => ({
          index,
          player1Bacteria: dish.player1Bacteria,
          player2Bacteria: dish.player2Bacteria,
        }))
      : [];

  return {
    id: rawMatch.id,
    code: rawMatch.code ?? null,
    createdAt: rawMatch.createdAt ?? null,
    startedAt: rawMatch.startedAt ?? null,
    endedAt: rawMatch.endedAt ?? null,
    turn: rawMatch.turn ?? 0,
    turnType: rawMatch.turnType ?? null,
    player1Score: rawMatch.player1Score ?? 0,
    player2Score: rawMatch.player2Score ?? 0,
    winner: rawMatch.winner ?? null,
    player1: playerSummary(rawMatch.player1),
    player2: playerSummary(rawMatch.player2),
    board,
  };
}