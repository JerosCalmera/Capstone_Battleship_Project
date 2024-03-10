
interface Props {
    shipInfo: string;
    shipDamage: string;
    turn: string;
    gameInfo: string;
    serverMessageLog: string;
    turnNumber: number;
    matchBegin: () => void;
    randomPlacement: () => void;
    matchStart: string;
}


const GameInfoBox: React.FC<Props> = ({serverMessageLog, turnNumber, turn, gameInfo, shipInfo, matchBegin, randomPlacement, matchStart}) => {


    return (
        <>
            {serverMessageLog === "Server: Rooms synced" ?
            <div className="gameInfoOuter">
                <div className="gameInfo">
                    <h3>Turn: ({turnNumber}) {turn.includes("Computer") ? "Computer" : turn}</h3>
                    <h3>{gameInfo}</h3>
                    {shipInfo.length === 60 && matchStart.length > 1 ? <button onClick={matchBegin} className="button">Confirm Ready</button> : null}
                    {shipInfo.length < 1 && matchStart.length > 2 ? <button onClick={randomPlacement} className="button">Random Ship Placement</button> : null}
                </div>
            </div> : null}
        </>
    )
}

export default GameInfoBox