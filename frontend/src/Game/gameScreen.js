import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import '../static/css/game/gameScreen.css';
import ExitGameModal from '../components/modal/ExitGameModal';
import useWebSocket from '../hooks/useWebSocket';
import api from '../services/api';
import tokenService from '../services/token.service';
import jwt_decode from 'jwt-decode';
import Chat from '../Game/Chat/Chat';
import ModalWinner from './modalWinner';
import PlayerColumn from './components/PlayerColumn';
import BoardStage from './components/BoardStage';
import TurnTimeline from './components/TurnTimeline';
import useTurnTracker from './hooks/useTurnTracker';
import {
  TURN_TIME_SECONDS,
  MAX_BACTERIA,
  PETRI_ADJACENCIES,
  TURN_SEQUENCE,
  ROUND_SIZE,
  TURN_ROUNDS,
  TURN_PHASE_META,
  QUICK_AMOUNT_OPTIONS,
} from './gameScreenHelper';


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
  const [boardFeedback, setBoardFeedback] = useState(null);
  const [currentUser] = useState(() => tokenService.getUser());
  const [lastMove, setLastMove] = useState({ source: null, target: null });
  const timerRef = useRef(null);
  const lastTurnRef = useRef(null);
  const previousBoardRef = useRef([]);

  const matchUpdate = useWebSocket(`/app/matches/watch/${id}`, `/topic/match/${id}`);

  const [showChat, setShowChat] = useState(true)
  const [muteChatMessage, setMuteChatMessage] = useState("Silenciar")


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

  const isPlayer1 = Boolean(currentUser && match?.player1?.username === currentUser.username);
  const isPlayer2 = Boolean(currentUser && match?.player2?.username === currentUser.username);
  let nickname = currentUser?.username ?? 'Invitado';
  if (match) {
    if (isPlayer1) {
      // Si soy el P1, uso el nickname del P1 (o su username si no tiene nick)
      nickname = match.player1?.nickname ?? match.player1?.username ?? 'Player 1';
    } else if (isPlayer2) {
      // Si soy el P2, uso el nickname del P2
      nickname = match.player2?.nickname ?? match.player2?.username ?? 'Player 2';
    }
  }
  const winnerName = match?.winner
    ? (match.winner === 1
        ? (match.player1?.nickname ?? match.player1?.username ?? 'Jugador 1')
        : (match.player2?.nickname ?? match.player2?.username ?? 'Jugador 2'))
    : null;
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

  const handleTimeUp = useCallback(async () => {
    try {
      if (isMyTurn && match?.id) {
        try {
          await api.put(`/api/v1/matches/${id}/endMatch`);
        } catch (err) {
          console.error('Unable to request endMatch on timeout', err);
        }
        try {
          const resp = await api.get(`/api/v1/matches/${id}`);
          setMatch(normaliseMatch(resp.data));
          return;
        } catch (err) {
          console.error('Unable to refresh match after endMatch', err);
        }
      }

      if (match) {
        const localWinner = isPlayer1 ? 2 : 1;
        setMatch((prev) => ({
          ...(prev ?? {}),
          winner: localWinner,
          endedAt: new Date().toISOString(),
        }));
      }
    } catch (err) {
      console.error('handleTimeUp error', err);
    } finally {
      setRunning(false);
      setTimeLeft(0);
    }
  }, [id, isMyTurn, isPlayer1, match]);

  useEffect(() => {
    if (timeLeft === 0) {
      handleTimeUp()
    }
  }, [handleTimeUp, timeLeft]);

  const handleBackToMenu = () => {
    const jwt = tokenService.getLocalAccessToken();
    const isAdmin = jwt ? jwt_decode(jwt).authorities?.includes("ADMIN") : false;
    navigate(isAdmin ? '/currentGames' : '/lobby');
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
  const isNextTurnFission = match? TURN_SEQUENCE[clampedTurnIndex + 1] === 'BINARY_FISSION': false;
  const isFissionsNextTurnContamination = match? TURN_SEQUENCE[clampedTurnIndex + 2] === 'CONTAMINATION': false;

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
      }
      setMatch(normaliseMatch(response.data));
      setTimeLeft(TURN_TIME_SECONDS);
      setRunning(true);
      if (propagationTurn && selectedSource !== null) {
        setLastMove({ source: selectedSource, target: null });
      }
      if (isNextTurnFission) {
        await api.put(`/api/v1/matches/${id}/nextTurn`);
      }
      if (isFissionsNextTurnContamination){
        await api.put(`/api/v1/matches/${id}/nextTurn`);
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
    } else {
      setSelectedSource(null);
      setMoveAmount(1);
      setBoardFeedback('Movimiento aplicado.');
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
      setSelectedSource(null);
      setSelectedTarget(null);
      setMoveAmount(1);
      setBoardFeedback(null);
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

  const handleShowChat = () => {
    if(showChat){
      setShowChat(false)
      setMuteChatMessage("Mostrar")
    }else{
      setShowChat(true)
      setMuteChatMessage("Silenciar")
    }
  }

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
  const canCancelSelection = canEditBoard && (selectedSource !== null || selectedTarget !== null);

  const handleQuickAmountSelect = useCallback((value) => {
    setMoveAmount(Math.min(value, maxMoveForSource));
  }, [maxMoveForSource]);

  const handleCancelSelection = useCallback(() => {
    setSelectedSource(null);
    setSelectedTarget(null);
    setMoveAmount(1);
    setBoardFeedback(null);
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
        <div className="chatTitle">CHAT</div>
        <div className="muteChat">
          <button onClick={handleShowChat} style={{
            backgroundColor: showChat ? "#a63e3e" : "#3a9150",
            color: "white",
            border: "none",
            borderRadius: "6px",
            fontWeight: "bold",
            fontFamily: "inherit",
            cursor: "pointer",
            fontSize: "20px",
            boxShadow: "0 2px 4px rgba(0,0,0,0.2)"
          }}>{muteChatMessage} chat</button>
        </div>
        {
          showChat && 
          <div className="chatList" style={{
            height: '85%'
            }}>
            <Chat nickname={nickname}
            id = {id}/>
          </div>
        }
        {waitingForPlayer && (
          <div className="chatRoomInfo">
            <p>Código de partida:</p>
            <div className="roomCode">{roomCode}</div>
            <p>Comparte este código para que se una otro jugador</p>
          </div>
        )}
      </aside>

      <main className="gameMainPanel">
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', padding: '0.6rem 0' }}>
          <div className="timer" style={{ textAlign: 'center' }}>Tiempo restante: {timeLeft} s</div>
        </div>
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

        <ModalWinner winner={winnerName} currentUser={nickname} onGoToMenu={handleBackToMenu} />
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
          totalTurns={totalTurnPhases}
          turnTrackOffset={turnTrackOffset}
          turnTrackRef={turnTrackRef}
          turnPhaseMeta={TURN_PHASE_META}
          roundSize={ROUND_SIZE}
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