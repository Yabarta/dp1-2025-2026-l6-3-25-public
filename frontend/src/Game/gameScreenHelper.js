export const TURN_TIME_SECONDS = 60;
export const MAX_BACTERIA = 5;

export const PETRI_ADJACENCIES = {
  0: [1, 2, 3],
  1: [0, 3, 4],
  2: [0, 3, 5],
  3: [0, 1, 2, 4, 5, 6],
  4: [1, 3, 6],
  5: [2, 3, 6],
  6: [3, 4, 5],
};

export const TURN_SEQUENCE = [
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

export const ROUND_SIZE = 10;
export const TURN_ROUNDS = Array.from({ length: Math.ceil(TURN_SEQUENCE.length / ROUND_SIZE) }, (_, roundIndex) =>
  TURN_SEQUENCE.slice(roundIndex * ROUND_SIZE, (roundIndex + 1) * ROUND_SIZE),
);

export const TURN_PHASE_META = {
  P1_PROPAGATION: { label: 'P1', description: 'Propagación jugador 1', className: 'turnPhase--p1' },
  P2_PROPAGATION: { label: 'P2', description: 'Propagación jugador 2', className: 'turnPhase--p2' },
  BINARY_FISSION: { label: 'F', description: 'Fisión binaria', className: 'turnPhase--binary' },
  CONTAMINATION: { label: 'C', description: 'Contaminación', className: 'turnPhase--contamination' },
};

export const QUICK_AMOUNT_OPTIONS = [1, 2, 3, 4, 5];
