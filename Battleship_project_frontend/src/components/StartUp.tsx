
interface Props {
    setPlayerName: React.Dispatch<React.SetStateAction<string>>;
    saveName: React.MouseEventHandler<HTMLButtonElement>;
    savedName: string;
    serverMessageLog: string;
    password: string;
    setPassword: React.Dispatch<React.SetStateAction<string>>;
    auth: () => void;
    generate: () => void;
    playerName: string;
    chat: string[];
    chatSend: () => void;
    setChatEntry: React.Dispatch<React.SetStateAction<string>>;
    leaderBoard: string[];
}

const StartUp: React.FC<Props> = ({ setPlayerName, saveName, savedName, serverMessageLog, password, setPassword, auth, generate, playerName, chat, chatSend, setChatEntry, leaderBoard }) => {


    return (
        <>
            {savedName != "name" && serverMessageLog != "Server: Room saved!" ? serverMessageLog != "Server: Rooms synced" ? serverMessageLog != "Server: Another player has started a room" ?
                <div className="startupOuter">
                    <h3>Please enter the room number, or press generate to generate one</h3>
                    <input className="input" name="room" value={password} onChange={(e) => setPassword(e.target.value)}></input>
                    <button className="button" onClick={auth}>Start</button><button className="button" onClick={generate}>Generate</button>
                </div>
                :
                <div className="startupOuter">
                    <h3>Please enter the room number....</h3>
                    <input className="input" name="room" value={password} onChange={(e) => setPassword(e.target.value)}></input>
                    <button className="button" onClick={auth}>Start</button>
                </div>
                :
                null : null}
            {savedName === "name" ?
                <div className="startupOuter">
                    <h3> Welcome to Solar Fury, Please enter your name....</h3>
                    <input className="input" name="name" value={playerName} onChange={(e) => setPlayerName(e.target.value)}></input>
                    <button className="button" onClick={saveName}>Save</button>
                </div>
                : null}
            <div className="chatBox">
                Chat: <br />
                {chat.map((message, index) => (
                    <li className="chatList" key={index}>{message}<br /></li>
                ))}
                <br />
                <input className="input" name="chat" onChange={(e) => setChatEntry(e.target.value)}></input>
                <button className="button" onClick={chatSend}>Send</button>
            </div>
            {serverMessageLog != "Server: Rooms synced" ?
                <div className="leaderBoard">
                    <h3>Top Players:</h3>
                    (one win = one level)
                    <div>
                        {leaderBoard.map((player, index) => (
                            <li className="chatList" key={index}><h4>{player}</h4><br /></li>))}
                    </div>
                </div> : null}
        </>
    )
}

export default StartUp
