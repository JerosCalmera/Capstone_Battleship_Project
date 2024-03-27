
interface Props {
    setPlayerName: React.Dispatch<React.SetStateAction<string>>;
    saveName: React.MouseEventHandler<HTMLButtonElement>;
    savedName: string;
    serverMessageLog: string;
    password: string;
    setPassword: React.Dispatch<React.SetStateAction<string>>;
    auth: () => void;
    generate: () => void;
    playVsComputer: () => void;
    playerName: string;
    ready: string;
    hidden: string;
    chat: string[];
    chatEntry: string;
    chatSend: () => void;
    setChatEntry: React.Dispatch<React.SetStateAction<string>>;
    leaderBoard: string[];
    nameValidated: boolean;
    roomNumberSave: any;
    player1Data: string;
}

const StartUp: React.FC<Props> = ({ player1Data, nameValidated, roomNumberSave, hidden, playVsComputer, chatEntry, setPlayerName, saveName, savedName, serverMessageLog, password, setPassword, auth, generate, playerName, chat, chatSend, setChatEntry, leaderBoard }) => {

    const chatBox = () => {
        if (player1Data != "Player 1")
            return "chatBoxOuter"
        else {
            return
        }
    }

    return (
        <>
            {nameValidated === true && !hidden.includes("Server: Room saved!") && !hidden.includes(roomNumberSave.current) ?
                <div className="startupOuter">
                    <h3>Please enter or generate a room code, or play against the computer</h3>
                    <input type="number" className="input" name="room" value={password} onChange={(e) => setPassword(e.target.value)}></input>
                    <button className="button" onClick={auth}>Save</button>
                    <button className="button" onClick={generate}>Generate</button>
                    <button className="button" onClick={playVsComputer}>Play against the computer</button>
                </div>
                :
                null}
            {nameValidated === false ?
                <div className="startupOuter">
                    <h3> Welcome to Solar Fury, Please enter your name</h3>
                    Player name: <input className="input" name="name" value={playerName} onChange={(e) => setPlayerName(e.target.value)}></input>
                    <button className="button" onClick={saveName}>Save</button>
                </div>
                : null}
            <div className={chatBox()}>
                <div className="chatBox">
                    Chat: <br />
                    {chat.map((message, index) => (
                        <li className="chatList" key={index}>{message}<br /></li>
                    ))}
                    <br />
                    <input className="input" name="chat" value={chatEntry} onChange={(e) => setChatEntry(e.target.value)}></input>
                    <button className="button" onClick={chatSend}>Send</button>
                </div>
            </div>
            {!hidden.includes("Server: Room synced") ?
                <div className="leaderBoardOuter">
                    <div className="leaderBoard">
                        <h3>Top 10 Players:</h3>
                        (one win = one level)
                        <div>
                            {leaderBoard.map((player, index) => (
                                <li className="chatList" key={index}><h4>{player}</h4><br /></li>))}
                        </div>
                    </div>
                </div> : null}
        </>
    )
}

export default StartUp
