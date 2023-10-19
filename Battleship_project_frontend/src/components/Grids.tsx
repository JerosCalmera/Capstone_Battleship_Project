import './Grids.css'
import { useEffect, useState } from "react";

interface StompClient {
    send(destination: string, headers?: Record<string, string>, body?: string): void;
}
interface Props {
    shipInfo: string;
    shipDamage: string;
    enemyShipDamage: string;
    stompClient: StompClient;
    savedName: string;
    player1Data: string;
    player2Data: string;
    placedShip: string;
    chat: string[];
    player2Name: string;
    miss: string;
    enemyMiss: string;
    turn: string;
    playerName: string;
    setPlacedShip: React.Dispatch<React.SetStateAction<string>>;
}

const Grids: React.FC<Props> = ({ playerName, turn, miss, enemyMiss, player2Name, chat, placedShip, setPlacedShip, player1Data, player2Data, savedName, shipInfo, shipDamage, enemyShipDamage, stompClient }) => {

    const [shipPlacement, setShipPlacement] = useState<boolean>(false)
    const [placedReadyShip, setPlacedReadyShip] = useState<string>("")
    const [shipSize, setShipSize] = useState<number>(0)
    const [shipToPlace, setShipToPlace] = useState<string>("")
    const [matchStart, setMatchStart] = useState<string>("Not Ready")

    const [carrier, setCarrier] = useState<number>(1)
    const [battleship, setBattleship] = useState<number>(2)
    const [cruiser, setCruiser] = useState<number>(3)
    const [destroyer, setDestroyer] = useState<number>(4)


    useEffect(() => {
        const shipType = "CarrierBattleshipCruiserDestroyer";
        const ship = placedShip;
        console.log(placedShip)
        if (ship.includes(shipType && savedName)) {
            if (ship.includes("Carrier")) { setCarrier(carrier - 1) }
            else if (ship.includes("Battleship")) { setBattleship(battleship - 1) }
            else if (ship.includes("Cruiser")) { setCruiser(cruiser - 1) }
            else if (ship.includes("Destroyer")) { setDestroyer(destroyer - 1) }
            setPlacedShip("");
            setShipToPlace("");
            stompClient.send("/app/startup", {}, JSON.stringify(playerName));
        }
    }, [placedShip])


    useEffect(() => {
        if (placedShip.includes("Invalid"))
            setPlacedReadyShip("")
        setShipToPlace("")
    }, [chat])

    const populateGrid = () => {
        const letter: Array<string> = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"]
        const value: Array<string> = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"]
        const end = [];
        for (let i = 0; i < 10; i++) {
            const buttons = [];
            for (let j = 0; j < 10; j++) {
                const cellValue: any = letter[i] + value[j];
                buttons.push(
                    <button key={cellValue}
                        onClick={() => clickedCell(cellValue)}
                        className={stylingCell(cellValue)}>**</button>);
            }
            end.push(
                <div>
                    <button className="endCell">
                        {letter[i]}
                    </button>
                    {buttons}
                </div>)
        }
        return end;
    }

    const stylingCell = (cellValue: string) => {
        if (miss.includes(cellValue)) { return "miss" }
        else if (shipDamage.includes(cellValue)) { return "cellDamaged" }
        else if (shipInfo.includes(cellValue)) { return "cellShip" }
        else if (placedReadyShip.includes(cellValue)) { return "cellPlaced" }
        else return "cell"
    }

    const stylingEnemyCell = (cellValue: string) => {
        if (enemyShipDamage.includes(cellValue)) { return "cellEnemyShip" }
        else if (enemyMiss.includes(cellValue)) { return "miss" }
        else return "cell"
    }

    const shipToPlaceStyle = (value: string) => {
        if (shipToPlace === "Carrier" && value === "Carrier") { return "shipToPlaceSelected " }
        else if (shipToPlace === "Battleship" && value === "Battleship") { return "shipToPlaceSelected " }
        else if (shipToPlace === "Cruiser" && value === "Cruiser") { return "shipToPlaceSelected " }
        else if (shipToPlace === "Destroyer" && value === "Destroyer") { return "shipToPlaceSelected " }
        else { return "shipToPlace" }
    }
    const populateEnemyGrid = () => {
        const letter: Array<string> = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"]
        const value: Array<string> = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"]
        const end = [];
        for (let i = 0; i < 10; i++) {
            const buttons = [];
            for (let j = 0; j < 10; j++) {
                const cellValue: any = letter[i] + value[j];
                buttons.push(
                    <button key={cellValue}
                        onClick={() => clickedEnemyCell(cellValue)}
                        className={stylingEnemyCell(cellValue)}>**</button>);
            }
            end.push(
                <div>
                    <button className="endCell">
                        {letter[i]}
                    </button>
                    {buttons}
                </div>)
        }
        return end;
    }
    const numbersBottom = () => {
        const numbers = [];
        for (let i = 0; i < 10; i++) {
            numbers.push(
                <button className="endCell">{i + 1}</button>
            )
        }
        return numbers
    }

    const clickedCell = (value: string) => {
        if (shipPlacement === false) {
            if (carrier > 0 && shipToPlace === "Carrier" ||
                battleship > 0 && shipToPlace === "Battleship" ||
                cruiser > 0 && shipToPlace === "Cruiser" ||
                destroyer > 0 && shipToPlace === "Destroyer") {
                stompClient.send("/app/placement", {}, JSON.stringify(value + shipSize + savedName));
                setPlacedReadyShip(placedReadyShip + value)
            }
            else if (shipInfo.includes(value)) { stompClient.send("/app/chat", {}, JSON.stringify("Invalid co-ordinate!")) }
            {
                if (placedReadyShip.length === shipSize * 2) {
                    setPlacedReadyShip("")
                    setShipToPlace("")
                }
            }
        }
    }


    const clickedEnemyCell = (value: string) => {
        if (shipPlacement === true) {
            if (turn === savedName) {
                if (enemyMiss.includes(value)) { null }
                else
                    stompClient.send("/app/gameData", {}, JSON.stringify(value + player2Name.slice(0, 4) + savedName));

            }
        }
    }
    const placeCarrier = () => {
        setShipSize(5);
        setShipToPlace("Carrier")
    }

    const placeBattleship = () => {
        setShipSize(4);
        setShipToPlace("Battleship")
    }

    const placeCruiser = () => {
        setShipSize(3);
        setShipToPlace("Cruiser")
    }

    const placeDestroyer = () => {
        setShipSize(2);
        setShipToPlace("Destroyer")
    }

    const matchBegin = () => {
        stompClient.send("/app/matchStart", {}, JSON.stringify("Match start"));
        setMatchStart("")
        setShipPlacement(true)
    }

    return (
        <>
            <div className="gameBoardOuterGreater">
                <div className="gameBoardOuter">
                    <div className="shipPlacementOuter">
                        <div className="shipPlacementInner">
                            {carrier > 0 ?
                                <ul>
                                    <button onClick={placeCarrier} className={shipToPlaceStyle("Carrier")}>*</button>
                                    <button onClick={placeCarrier} className={shipToPlaceStyle("Carrier")}>*</button>
                                    <button onClick={placeCarrier} className={shipToPlaceStyle("Carrier")}>*</button>
                                    <button onClick={placeCarrier} className={shipToPlaceStyle("Carrier")}>*</button>
                                    <button onClick={placeCarrier} className={shipToPlaceStyle("Carrier")}>*</button>
                                    <h4>Carrier: x{carrier}</h4><br />
                                </ul>
                                : null}
                            {battleship > 0 ?
                                <ul>
                                    <button onClick={placeBattleship} className={shipToPlaceStyle("Battleship")}>*</button>
                                    <button onClick={placeBattleship} className={shipToPlaceStyle("Battleship")}>*</button>
                                    <button onClick={placeBattleship} className={shipToPlaceStyle("Battleship")}>*</button>
                                    <button onClick={placeBattleship} className={shipToPlaceStyle("Battleship")}>*</button>
                                    <h4>Battleship: x{battleship}</h4><br />
                                </ul>
                                : null}
                            {cruiser > 0 ?
                                <ul>
                                    <button onClick={placeCruiser} className={shipToPlaceStyle("Cruiser")}>*</button>
                                    <button onClick={placeCruiser} className={shipToPlaceStyle("Cruiser")}>*</button>
                                    <button onClick={placeCruiser} className={shipToPlaceStyle("Cruiser")}>*</button>
                                    <h4>Cruiser: x{cruiser}</h4> <br />
                                </ul>
                                : null}
                            {destroyer > 0 ?
                                <ul>
                                    <button onClick={placeDestroyer} className={shipToPlaceStyle("Destroyer")}>*</button>
                                    <button onClick={placeDestroyer} className={shipToPlaceStyle("Destroyer")}>*</button>
                                    <h4>Destroyer: x{destroyer}</h4><br />
                                </ul>
                                : null}
                        </div>
                        {shipInfo.length === 60 && matchStart.length > 1 ? <button onClick={matchBegin} className="button">Confirm Ready</button> : null}
                    </div>
                    <div className="gameBoardRender">
                        <h2>{player1Data}</h2>
                        <ul>
                            {populateGrid()}
                            <button name="end" className="endCellCorner">*</button>
                            {numbersBottom()}
                        </ul>
                    </div >
                    <div className="gameBoardRender2">
                        <h2>{player2Data}</h2>
                        <ul>
                            {populateEnemyGrid()}
                            <button name="end" className="endCellCorner">*</button>
                            {numbersBottom()}
                        </ul>
                    </div>
                </div >
            </div >
        </>
    )
}

export default Grids