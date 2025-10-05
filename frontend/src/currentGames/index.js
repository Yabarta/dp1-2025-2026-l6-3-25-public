import {Button, Table} from "reactstrap";
//import tokenService from "../services/token.service";
import {Link} from 'react-router-dom';

//const jwt = tokenService.getLocalAccessToken();

//Mock game data
const games = [
    {
        id: 1,
        creator: {nickname: "Player1", email:"player1@gmail.com"},
        player1: {nickname: "Player1", email:"player1@gmail.com"},
        player2: {nickname: "Player3", email:"player3@gmail.com"},
        createdAt: new Date(), startedAt: new Date(), endedAt: null,
        code: null,
        turn: 0, turnType:""
    },
    {
        id: 2,
        creator: {nickname: "Player2", email:"player2@gmail.com"},
        player1: {nickname: "Player4", email:"player4@gmail.com"},
        player2: {nickname: "Player2", email:"player2@gmail.com"},
        createdAt: new Date(), startedAt: new Date(), endedAt: null,
        code: "ABCD",
        turn: 23, turnType:""
    }
]

export default function CurrentGames() {
    
    const gameList =
    games.map((g) => {
        return(
            <tr key={g.id}>
                <td className="text-center">{g.player1.nickname}</td>
                <td className="text-center">{g.player2.nickname}</td>
                <td className="text-center">{g.startedAt.toLocaleString()}</td>
                <td className="text-center">{g.code}</td>
                <td>
                    <Button
                        size="sm"
                        color="primary"
                        aria-label={"spectate-" + g.id}
                        tag={Link}
                        to={"/game/" + g.id}
                    >
                        Spectate game
                    </Button>
                </td>
            </tr>
        );
    });

    return (
        <div>
            <div className="admin-page-container">
                <h1 className="text-center">Current Games</h1>
                <div>
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
        </div>
    );

}