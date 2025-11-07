import { useState } from "react";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { Link } from 'react-router-dom';
import { Button } from "reactstrap";
import styles from '../static/css/home/gameListing.css'


const jwt = tokenService.getLocalAccessToken();

export default function NotStartedGames(){
    const [message, setMessage] = useState(null)
    const [visible, setVisible] = useState(false)
    const [games, setGames] = useFetchState(
        [],
        '/api/v1/matches/notStarted',
        jwt,
        setMessage,
        setVisible
    )

    const notStartedGamesList = games.map((g) => {
        return(
            <div className="join-game">
                <div className="creator-name">{ g.creator.nickname }</div>
                <Button
                    size="sm"
                    color="primary"
                    aria-label={"spectate-" + g.id}
                    tag={Link}
                    to={"/game/" + g.id}
                >
                    Unirse a la partida
                </Button>
            </div>
        )
    })

    return(
        <div>
            
            <div className="listing-page-container">
                <h1 className="text-center" style={{
                color: "white",
            }}>Join game</h1>
                <div>
                    { notStartedGamesList }
                </div>
            </div>
        </div>
    )
}
