import React, { useState } from "react";
import HeaderMetrics from "../components/HeaderMetrics";
import Podium from "../components/Podium";
import LeaderboardTable from "../components/LeaderBoardTable";
import getErrorModal from "../../util/getErrorModal";
import '../styles/PetrisStats.css';

import useFetchState from "../../util/useFetchState";
import tokenService from "../../services/token.service";

const jwt = tokenService.getLocalAccessToken()

export default function StatisticRanking() {
  const [message, setMessage] = useState(null)
  const [visible, setVisible] = useState(false)

  const [globalStats, setGlobalStats, globalLoading] = useFetchState(
    [],
    `/api/v1/statistics/global`,
    jwt,
    setMessage,
    setVisible
  )

  const [leaderboards, setLeaderBoard, boardLoading] = useFetchState(
    [],
    `/api/v1/ranking`,
    jwt,
    setMessage,
    setVisible
  )

  const modal = getErrorModal(setVisible, visible, message);
  const isLoading = globalLoading || boardLoading

  const topThree = leaderboards?.slice?.(0, 3) ?? [];
  const restOfBoard = leaderboards?.slice?.(3) ?? [];

  const [selectedScientists, setSelectedScientists] = useState([]);

  // Lógica de Negocio: Añadir/Quitar jugadores al comparador
  const toggleSelection = (scientist) => {
    const isSelected = selectedScientists.find(s => s.id === scientist.id);

    if (isSelected) {
      // Si ya está, lo quitamos
      setSelectedScientists(prev => prev.filter(s => s.id !== scientist.id));
    } else {
      // Si no está, lo añadimos (controlando máximo 2)
      if (selectedScientists.length < 2) {
        setSelectedScientists(prev => [...prev, scientist]);
      } else {
        alert("Solo puedes comparar 2 muestras biológicas a la vez.");
      }
    }
  };

  const clearSelection = () => setSelectedScientists([]);

  if (isLoading) {
        return (
            <div className="loadingOverlay">
                {modal}
                <div className="loadingCard">
                    <div className="loadingTitle">Cargando datos de ranking</div>
                    <div className="loadingSubtitle">Un momento, estamos cargando los datos globales</div>
                </div>
            </div>
        );
    }

  return (
    <div className="petris-container">
      {/* Fondo Decorativo */}
      <div className="bio-background"></div>

      {/* Cabecera */}
      <HeaderMetrics 
        gamesSize={globalStats.totalGamesPlayed}
        timePlayed={globalStats.totalTimePlayed}
        sarcines={globalStats.totalSarcinasCreated}
        playersRegistered={globalStats.totalPlayers}
      />

      {/* Top 3 */}
      <Podium players={topThree} jwt={jwt}/>

      {/* Tabla del Ranking */}
      <LeaderboardTable 
        players={restOfBoard} 
        selectedIds={selectedScientists.map(s => s.id)} 
        onToggle={toggleSelection} 
        jwt={jwt}
      />
    </div>
  );
}