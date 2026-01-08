import React, { useCallback, useState } from "react";
import { Progress } from "reactstrap";

import useFetchState from "../../util/useFetchState";
import tokenService from "../../services/token.service";
import '../../static/css/admin/progressBar.css'
import { useParams } from "react-router-dom";
import jwt_decode from "jwt-decode";

const jwt = tokenService.getLocalAccessToken();

export default function AchievementProgressBar ({ achievement }) {
  
  const progress = 30
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);


  const { username } = useParams()
  const [currentPlayer, setCurrentPlayer] = useState(() => {
          if (!jwt) return username ?? "";
          try {
              return jwt_decode(jwt)?.sub ?? (username ?? "");
          } catch (e) {
              console.error("Invalid JWT", e);
              return username ?? "";
          }
      });
  const playerUrl = currentPlayer ? `/api/v1/players/user/${encodeURIComponent(currentPlayer)}` : "";
  const [playerData, setPlayerData, playerLoading] = useFetchState({}, playerUrl, jwt, setMessage, setVisible);
  const statsUrl = playerData?.id ? `/api/v1/players/${playerData.id}/statistics` : "";
  const [playerStats, , statsLoading] = useFetchState([], statsUrl, jwt, setMessage, setVisible, playerData?.id);

  const getStatValue = useCallback((name) => {
          if (!playerStats) return 0;
              const sanitizedName = name.toLowerCase();
              const lowerCamelCaseName = sanitizedName.replace(/_([a-z])/g, (g) => g[1].toUpperCase());
              const value = playerStats?.[lowerCamelCaseName];
              if (value === null || value === undefined) return 0;
              return value;
          
      }, [playerStats]);
  
  const achievementProgress = useCallback((achievement) => {
          const progress = Math.round(getStatValue(achievement.statisticName));
          const target = achievement.valor;
          return Math.min(progress, target)
  }, [getStatValue]);
  
  

  const validatedProgress = Math.min(achievement.valor, Math.max(0, achievementProgress));
  return (
    <div 
      className="progress-container"
      role="progressbar"
      aria-valuenow={achievementProgress(achievement)}
      aria-valuemin="0"
      aria-valuemax={achievement.valor}
    >
      <div 
        className="progress-filler" 
        style={{ width: `${achievementProgress(achievement)/achievement.valor*100}%` }}
      >
        <span className="progress-label">{`${achievementProgress(achievement)}/${achievement.valor}`}</span>
      </div>
    </div>
  );
}