import Stomp, {} from "stompjs";
import SockJS from "sockjs-client";
import { useEffect, useRef, useState } from "react";
import Grids from "../components/Grids";
import StartUp from "../components/StartUp";
import LoadingSplash from "../components/LoadingSplash";


function GameBoard() {
    const BASE_URL = "http://localhost"

    const [stompClient, setStompClient] = useState<Stomp.Client>(Stomp.over(new SockJS(`${BASE_URL}:8081/game`)));
    const [serverStatus, setServerStatus] = useState(false)
    const [attemptReconnect, setAttemptReconnect] = useState(0)
    const [serverMessageLog, serverSetMessageLog] = useState("")
    const [hidden, setHidden] = useState("")

    const [leaderBoard, setLeaderBoard] = useState<string[]>([])

    const [shipInfo, setShipInfo] = useState<string>("")
    const [shipDamage, setShipDamage] = useState<string>("")

    const [enemyShipDamage, setEnemyShipDamage] = useState<string>("")

    const [damageCheck, setDamageCheck] = useState<string>("")

    const [missCheck, setMissCheck] = useState<string>("")
    const [miss, setMiss] = useState<string>("")
    const [enemyMiss, setEnemyMiss] = useState<string>("")

    const [password, setPassword] = useState<string>("")
    const [passwordEntry, setPasswordEntry] = useState<string>("")

    const [playerName, setPlayerName] = useState<string>("")
    const [savedName, setSaveName] = useState<string>("name")
    const [ready, setReady] = useState<string>("name")

    const [chat, setChat] = useState<string[]>(["", "", "", "", "", "", "", "", "", ""])
    const [chatEntry, setChatEntry] = useState<string>("")

    const [player1Data, setPlayer1Data] = useState<string>("Player 1")
    const [player2Data, setPlayer2Data] = useState<string>("Player 2")
    const [player2Name, setPlayer2Name] = useState<string>("Player 2")

    const [placedShip, setPlacedShip] = useState<string>("")
    const [cellStorage, setCellStorage] = useState<string>("")

    const [gameInfo, setGameInfo] = useState<string>("")
    const [turn, setTurn] = useState<string>("Ship Placement Phase")
    const [turnNumber, setTurnNumber] = useState<number>(-1)

    const [bugReport, setBugReport] = useState<number>(0)
    const [bugReportInput, setBugReportInput] = useState<string>("")

    const [startUpFlash, setStartUpFlash] = useState<number>(1)
    const [gameFlash, setGameFlash] = useState<number>(1)


    useEffect(() => {
        const port = 8081;
        const socket = new SockJS(`${BASE_URL}:${port}/game`);
        const client = Stomp.over(socket);

        client.connect({}, () => {
            console.log("Connected to server");
            client.subscribe("/topic/connect", (message: any) => {
                serverSetMessageLog(message.body.slice(12, -2))
            });
            client.subscribe("/topic/hidden", (message: any) => {
                const newMessage: string = message.body.slice(12, -2)
                if (newMessage.includes(passwordEntry)) {
                setHidden(message.body.slice(16, -2))}
            });
            client.subscribe("/topic/gameInfo", (message: any) => {
                const newMessage: string = message.body.slice(12, -2)
                if (newMessage.includes(passwordEntry)) {
                setGameInfo(message.body.slice(16, -2))}
            });
            client.subscribe("/topic/gameData", (message: any) => {
                const newMessage: string = message.body.slice(12, -2)
                if (newMessage.includes(passwordEntry)) {
                setCellStorage(message.body.slice(16, -2))}
            });

            client.subscribe("/topic/turn", (message: any) => {
                const newMessage: string = message.body.slice(12, -2)
                if (newMessage.includes(passwordEntry)) {
                setTurn(message.body.slice(16, -2))}
            });
            client.subscribe("/topic/gameData2", (message: any) => {
                const newMessage: string = message.body.slice(12, -2)
                if (newMessage.includes(passwordEntry)) {
                setShipDamage(message.body.slice(16, -2))}
            });
            client.subscribe("/topic/placement", (message: any) => {
                const newMessage: string = message.body.slice(12, -2)
                if (newMessage.includes(passwordEntry)) {
                setShipDamage(message.body.slice(16, -2))}
            });
            client.subscribe("/topic/miss", (message: any) => {
                const newMessage: string = message.body.slice(12, -2)
                if (newMessage.includes(passwordEntry)) {
                setMissCheck(message.body.slice(16, -2))}
            });
                // if (newMessage.includes("/global" || "/Global") || newMessage.includes("Lobby") || !newMessage.includes("Admin:")) {
                //         newMessage = message.body.slice(16, -2);
                //         if (!newMessage.includes("Admin")){
                //         newMessage = newMessage.replace(/\/global|\/Global/g, "[Global]")}
                //         setChat((prevChat) => {
                //             const updatedChat = [...prevChat, newMessage];
                //             return updatedChat.slice(-10);}
                // )}

            client.subscribe("/topic/chat", (message: any) => {
                chatParse(message)
                });

            client.subscribe("/topic/playerData1", (message: any) => {
                const newMessage: string = message.body.slice(12, -2)
                if (newMessage.includes(passwordEntry)) {
                setPlayer1Data(message.body.slice(16, -2))}
            })
            client.subscribe("/topic/playerData2", (message: any) => {
                const newMessage: string = message.body.slice(12, -2)
                if (newMessage.includes(passwordEntry)) {
                setPlayer2Data(message.body.slice(16, -2))}
            });

            client.subscribe("/topic/computer", () => {
            });

            client.subscribe("/topic/randomPlacement", () => {
            });

            client.subscribe("/topic/leaderBoard", (message: any) => {
                const leaderBoardEntry: string = message.body.slice(12, -2)
                setLeaderBoard((prevLeader) => {
                    const updatedLeader = [...prevLeader, leaderBoardEntry];
                    return updatedLeader.slice(-10)
                })
            }
            );

            client.send("/app/hello", {}, JSON.stringify(`Client Connected on ${port}`));
            setServerStatus(true);

            client.subscribe("/topic/gameUpdate", (message: any) => {
                const newMessage: string = message.body.slice(12, -2)
                if (newMessage.includes(passwordEntry)) {
                setPlayer1Data(message.body.slice(16, -2))}
            });

            client.subscribe("/topic/enemyDamage", (message: any) => {
                const newMessage: string = message.body.slice(12, -2)
                if (newMessage.includes(passwordEntry)) {
                setDamageCheck(message.body.slice(16, -2))}
            });

            client.subscribe("/topic/startup", (message: any) => {
                const newMessage: string = message.body.slice(12, -2)
                if (newMessage.includes(passwordEntry)) {
                setPlayer1Data(message.body.slice(16, -2))}
            });

            client.subscribe("/topic/placement2", (message: any) => {
                const newMessage: string = message.body.slice(12, -2)
                if (newMessage.includes(passwordEntry)) {
                setPlacedShip(message.body.slice(16, -2))}
            });

            client.subscribe("/topic/bugReport", () => {
            });

            client.ws.onclose = () => {
                (console.log("Connection terminated"))
                setServerStatus(false)
            };
            setStompClient(client)
        });
    }, [attemptReconnect])

    useEffect(() => {
        if (player1Data.includes(savedName)) {
            setPlayer1Data(player1Data)
            setPlayer2Data(player2Data)
            setPlayer2Name(player2Data)
        }
        else if (player2Data.includes(savedName)) {
            setPlayer1Data(player2Data)
            setPlayer2Data(player1Data)
            setPlayer2Name(player1Data)
        }
    }, [player2Data, serverMessageLog])


    useEffect(() => {
        setTurnNumber(turnNumber + 1)
        console.log("Turn number use effect:", turnNumber);
    }, [turn])

    useEffect(() => {
        if (missCheck.includes(savedName)) {
            setMiss(miss + missCheck);
            console.log(miss)
        }
        else if (!missCheck.includes(savedName)) {
            setEnemyMiss(enemyMiss + missCheck)
            console.log(enemyMiss)
        }
    }, [missCheck])

    useEffect(() => {
        console.log(leaderBoard)
        if (leaderBoard.length < 1 && serverMessageLog === "Game server ready....") {
            stompClient.send("/app/leaderBoard", {}, JSON.stringify("Game start"));
            stompClient.unsubscribe("/app/leaderBoard")
        }
    }, [serverMessageLog, leaderBoard]);


    useEffect(() => {
        const toTrim = cellStorage;
        if (toTrim.includes(savedName)) {
            const trimmed: string = toTrim.replace(savedName, '');
            setShipInfo(trimmed);
        }
    }, [cellStorage]);


    useEffect(() => {
        if (!damageCheck.includes(savedName)) {
            setEnemyShipDamage(enemyShipDamage + damageCheck.slice(0, 2))
        }
        else {
            setShipDamage(shipDamage + damageCheck.slice(0, 2))
        }
        console.log(shipInfo)
    }, [damageCheck]);

    useEffect(() => {
        if (hidden.includes(savedName)) {
            setSaveName("name");
            setHidden("");
        }
    }, [hidden, chat, serverMessageLog]);

    const gameFlashSave = useRef(gameFlash);

    useEffect(() => {
        gameFlashSave.current = gameFlash
    }, [chat]);

    const roomNumberSave = useRef(passwordEntry);

    useEffect(() => {
        roomNumberSave.current = passwordEntry
    }, [turnNumber, chat]);


    const auth = () => {
        if (password.length < 4) {
            stompClient.send("/app/chat", {}, JSON.stringify("Admin: Sorry room codes must be minimum of 4 characters long!"));
        }
        else {
            setPasswordEntry(password)
            stompClient.send("/app/room", {}, JSON.stringify(password));
        }
    }

    const generate = () => {
        const randomNumber = Math.floor(Math.random() * 10000)
        const roomNumber = randomNumber.toString().padStart(4, "0");
        setPasswordEntry(roomNumber)
        setPassword(roomNumber)
        stompClient.send("/app/room", {}, JSON.stringify(roomNumber));
    }

    const saveName = () => {
        if (playerName.length < 5) {
            stompClient.send("/app/chat", {}, JSON.stringify("Admin: Sorry usernames must be minimum of 5 characters long!"));
        }
        else {
            setSaveName(playerName);
            stompClient.send("/app/name", {}, JSON.stringify(playerName));
            console.log(playerName)
            setReady("ready");
        }

    }
    const chatSend = () => {
        if (passwordEntry.length < 4) {
            stompClient.send("/app/chat", {}, JSON.stringify("Game[Lobby] Guest: " + chatEntry));}
        else {
            stompClient.send("/app/chat", {}, JSON.stringify(passwordEntry + savedName + ": " + chatEntry));}
        setChatEntry("")
    }

    const chatParse = (message: any) => {
        console.log("gameflash " + gameFlashSave.current)
    let newMessage: string = message.body.slice(12, -2);
        if (gameFlashSave.current === 1) {
            setChat((prevChat) => {
            const updatedChat = [...prevChat, newMessage];
            return updatedChat.slice(-10);
        });
        } else if (newMessage.includes(roomNumberSave.current) && roomNumberSave.current.length > 0) {
                newMessage = message.body.slice(16, -2);
                setChat((prevChat) => {
                const updatedChat = [...prevChat, newMessage];
                return updatedChat.slice(-10);
            });
        };
    }

    const restart = () => {
        stompClient.send("/app/restart", {}, JSON.stringify(passwordEntry));
        location.reload()
    }
    
    const serverStatusStyle = () => {
        if (serverMessageLog != "Server: Rooms synced")
            return
        else {
            return "serverStatus"
        }
    }

    const playVsComputer = () => {
        const randomNumber = Math.floor(Math.random() * 9000) + 1000;
        const roomNumber = randomNumber.toString().padStart(4, "0");
        setPasswordEntry(roomNumber)
        setPassword(roomNumber)
        stompClient.send("/app/computer", {}, JSON.stringify(roomNumber));
    }
    
    const bugReporting = () => {
        if (bugReport === 0){
        setBugReport(1)}
        else
        setBugReport(0);
    }

    const gameFlashScreen = () => {
        if (gameFlash === 0){
        setGameFlash(1)}
        else
        setGameFlash(0);
    }

    const startUpFlashScreen = () => {
        if (startUpFlash === 0){
        setStartUpFlash(1)}
        else
        setStartUpFlash(0);
    }

    const sendBugReport = () => {
        stompClient.send("/app/bugReport", {}, JSON.stringify("DATE: " + Date() + ", USER: " + savedName + ", REPORT: "  + bugReportInput));
        setBugReport(0)
    }

    const bugReportingRender = () => {
        return (
        <div className="bugReportPageFade">
            <div className="bugReportOuter">
                <div className="bugReport">
                    <div className="cancelBox">
                        <button className="button" onClick={bugReporting}>X</button>
                    </div>
                    <h3>Please write your bug report (or message) in as much detail as possible</h3>
                    <input className="bugReportInputBox" name="room" value={bugReportInput} onChange={(e) => setBugReportInput(e.target.value)}></input><br />
                        <button className="button" onClick={sendBugReport}>Send</button>
                </div>
            </div>
        </div>
        )
    }

    
    const startUpFlashRender = () => {
        return (
        <div className="bugReportPageFade">
            <div className="bugReportOuter">
                <div className="gameFlash">
                <h3>Welcome to Solar Fury! A multiplayer battleship game with a sci-fi theme! <br />
                    <div className="gameFlashBody">
                    <br />
                    To get started, enter a name in the prompt, and then you have the option of either entering a room code if a friend has started a room, creating a new room code or randomly generating one. <br />
                    <br />
                    When the code is entered for another persons room you will join them in that game room, alternatively if you are creating the room, simply share the code with the other player. <br />
                    <br />
                    If you wish, you can play against the computer by selecting that option, be forewarned however, the computer is no pushover!<br />
                    <br />
                    Your username is saved in the cloud, so you can return and continue playing and leveling up by re-entering that username.
                    </div>
                    </h3>
                        <button className="button" onClick={startUpFlashScreen}>Ok</button>
                </div>
            </div>
        </div>
        )
    }

    const gameFlashRender = () => {
        return (
        <div className="bugReportPageFade">
            <div className="bugReportOuter">
                <div className="gameFlash">
                <h3>Time to play! <br />
                    <div className="gameFlashBody">
                    <br />
                    First of all, place your ships by clicking them from the left selection, and then click two spaces on your grid to place them,
                    the ships will then autocomplete in the direction you clicked, alternatively click "Random Placement" to have the computer place your ships for you.
                    Once all your ships are placed, click "Ready", once both players are ready the match will begin! <br />
                    <br />
                    The first player will be picked randomly, then click on your opponent's board on your turn to shoot at their ships. The first player to destroy all their
                    opponents' ships will be the winner! And you will gain a level that will be shown on the leaderboard if you are in the top ten! <br />
                    <br />
                    You can chat to other players using the chat box at the bottom, if you wish to submit a bug report or leave a message for the developer, click the box on the top left. <br />
                    <br />
                    </div>
                    Good luck! And have fun!
                    </h3>
                        <button className="button" onClick={gameFlashScreen}>Start</button>
                </div>
            </div>
        </div>
        )
    }

    return (
        <>
            {bugReport === 1 ? bugReportingRender() : null}
            {serverStatus == true && startUpFlash === 1 ? startUpFlashRender() : null}
            <div className={serverStatusStyle()}>
                {serverStatus == true ? <h5>Connected to game server</h5> :
                    <>
                        <h5>Not connected to game server</h5><LoadingSplash attemptReconnect={attemptReconnect} setAttemptReconnect={setAttemptReconnect}/>
                        <button className="button" onClick={() => setAttemptReconnect(attemptReconnect + 1)}>Reconnect</button></>
                }
                <h5>{serverMessageLog}</h5>
                <button className="button" onClick={restart}>Restart</button>
                <button className="button" onClick={bugReporting}>Bug Report/Msg Dev</button>

            </div>
            {serverMessageLog === "Server: Room saved!" && passwordEntry.length < 1 ? serverSetMessageLog("Server: Another player has started a room") : null}
            {serverMessageLog === "Server: Room saved!" && savedName != "name" && passwordEntry.length > 0 ?
                <div className="startupOuter">
                    <h3 >Room number: {passwordEntry}</h3 >
                    <h3>Waiting on other player.....</h3></div >
                : serverMessageLog === "Server: Rooms synced"  && savedName != "name" && passwordEntry.length > 0 ?
                    <div>
                        {gameFlash === 1 ? gameFlashRender() : null}
                        <Grids setChat={setChat} chat={chat} gameInfo={gameInfo} turnNumber={turnNumber} serverMessageLog={serverMessageLog} playerName={playerName} turn={turn} miss={miss} enemyMiss={enemyMiss} player2Name={player2Name}
                            placedShip={placedShip} player1Data={player1Data} setPlacedShip={setPlacedShip}
                            player2Data={player2Data} savedName={savedName} shipInfo={shipInfo}
                            shipDamage={shipDamage} enemyShipDamage={enemyShipDamage}
                            stompClient={stompClient} />
                    </div> : null}

            <StartUp playVsComputer={playVsComputer} chatEntry={chatEntry} hidden={hidden} ready={ready} savedName={savedName} serverMessageLog={serverMessageLog} password={password}
                setPassword={setPassword} auth={auth} generate={generate} playerName={playerName} chat={chat}
                saveName={saveName} chatSend={chatSend} setPlayerName={setPlayerName} setChatEntry={setChatEntry}
                leaderBoard={leaderBoard} />
        </>
    )
    }
    export default GameBoard