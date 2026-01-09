import { Table } from "reactstrap";
import tokenService from "../services/token.service";
import { Link } from 'react-router-dom';
import useFetchState from "../util/useFetchState";
import { useState } from "react";
import "../static/css/admin/adminPage.css";
import "../static/css/auth/authButton.css";

const jwt = tokenService.getLocalAccessToken();


export default function CurrentGames() {
    const [message, setMessage] = useState(null)
    const [visible, setVisible] = useState(false)
    const[currentMatches, setCurrentMatches] = useFetchState(
        [],
        `/api/v1/matches/current`,
        jwt,
        setMessage,
        setVisible
    )
    

    const gameList =
    currentMatches.map((g) => {
        return(
            <tr key={g.id}>
                <td className="text-center">{g.player1.nickname}</td>
                <td className="text-center">{g.player2.nickname}</td>
                <td className="text-center">{g.startedAt.toLocaleString()}</td>
                <td className="text-center">{g.code}</td>
                <td className="text-center">
                    <Link
                        className="auth-button blue"
                        aria-label={"spectate-" + g.id}
                        to={"/game/" + g.id}
                        style={{ padding: '0.5rem 1rem', fontSize: '0.8rem', minWidth: 'auto' }}
                    >
                        Spectate game
                    </Link>
                </td>
            </tr>
        );
    });

    return (
        <div className="admin-page-container">
            <h1 className="text-center">Current Games</h1>
            <div style={{ width: '100%', maxWidth: '1200px' }}>
                <Table aria-label="current-games" className="mt-4">
                    <thead>
                        <tr>
                            <th className="text-center">Player 1</th>
                            <th className="text-center">Player 2</th>
                            <th className="text-center">Started</th>
                            <th className="text-center">Code</th>
                            <th className="text-center">Actions</th>
                        </tr>
                    </thead>
                    <tbody>{gameList}</tbody>
                </Table>
            </div>
        </div>
    );

}