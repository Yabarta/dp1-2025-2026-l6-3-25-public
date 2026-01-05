import { useEffect, useRef, useState } from 'react';

export default function useTurnTracker(activeRoundIndex, currentPhaseIndexInRound, currentTurnIndex) {
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
