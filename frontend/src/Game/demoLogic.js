
export const turnOrder = ["J1", "J2", "FB", "J2", "J1", "FB", "J1", "J2", "FB", "FC"];

export const initialPlayers = [
  { id: 1, name: "Jugador 1", score: 0 },
  { id: 2, name: "Jugador 2", score: 0 },
];

export const initialBoard = [
  // Fila 1 (2 discos)
  { id: 0, j1: 0, j2: 0, adyacentes: [1, 2, 3] },
  { id: 1, j1: 0, j2: 0, adyacentes: [0, 3, 4] },

  // Fila 2 (3 discos)
  { id: 2, j1: 1, j2: 0, adyacentes: [0, 3, 5] },
  { id: 3, j1: 0, j2: 0, adyacentes: [0, 1, 2, 4, 5, 6] },
  { id: 4, j1: 0, j2: 1, adyacentes: [1, 3, 6] },

  // Fila 3 (2 discos)
  { id: 5, j1: 0, j2: 0, adyacentes: [2, 3, 6] },
  { id: 6, j1: 0, j2: 0, adyacentes: [3, 4, 5] },
];

export const initialGameState = {
  players: initialPlayers,
  board: initialBoard,
  currentPhaseIndex: 0,
  winner: null,
};

// Game/gamePhases.js (habría que hacer otro archivo por cohesión)

export function nextPhase(gameState) {
  const nextIndex = (gameState.currentPhaseIndex + 1) % 10;
  const nextPhase = gameState.turnOrder[nextIndex];
  let newState = { ...gameState, currentPhaseIndex: nextIndex };

  switch (nextPhase) {
    case "FB":
      newState = doFB(newState);
      break;
    case "FC":
      newState = doFC(newState);
      break;
    default:
      break;
  }

  return newState;
}

// Fase FB (bacterias se reproducen si el disco es controlado por un solo jugador)
function doFB(gameState) {
  const newBoard = gameState.board.map((disco) => {
    if (disco.j1 > 0 && disco.j2 === 0 && disco.j1 < 5) {
      return { ...disco, j1: disco.j1 + 1 };
    } else if (disco.j2 > 0 && disco.j1 === 0 && disco.j2 < 5) {
      return { ...disco, j2: disco.j2 + 1 };
    }
    return disco;
  });

  return { ...gameState, board: newBoard };
}

// Fase FC (comparar bacterias y sumar puntos)
function doFC(gameState) {
  const newPlayers = gameState.players.map((p) => ({ ...p }));

  gameState.board.forEach((disco) => {
    if (disco.j1 > disco.j2 && gameState.players[0].score < 9) {
      newPlayers[0].score += 1;
    } else if (disco.j2 > disco.j1 && gameState.players[1].score < 9) {
      newPlayers[1].score += 1;
    }
  });

  return { ...gameState, players: newPlayers };
}
