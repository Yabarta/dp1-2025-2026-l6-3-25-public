

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
  currentTurn: 1,
  winner: null,
};
