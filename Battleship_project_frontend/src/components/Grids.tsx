import { ReactComponentElement, useEffect, useState } from "react";

interface StompClient {
    send(destination: string, headers?: Record<string, string>, body?: string): void;
}
interface Props {
    shipInfo: string;
    shipDamage: string;
    enemyShipInfo: string;
    enemyShipDamage: string;
    stompClient: StompClient;
    savedName: string;
    player1Data: string;
    player2Data: string;
    carrier: number;
    battleship: number;
    cruiser: number;
    destroyer: number;
    placedShip: string;
    chat: string[];
    player2Name: string;
}

const Grids: React.FC<Props> = ({ setEnemyShipDamage, setShipDamage, player2Name, chat, placedShip, destroyer, cruiser, battleship, carrier, player1Data, player2Data, savedName, shipInfo, shipDamage, enemyShipInfo, enemyShipDamage, stompClient }) => {

    const [shipPlacement, setShipPlacement] = useState<boolean>(false)
    const [placedReadyShip, setPlacedReadyShip] = useState<string>("")
    const [shipSize, setShipSize] = useState<number>(0)
    const [shipToPlace, setShipToPlace] = useState<string>("")


    useEffect(() => {
        if (placedShip.includes("Invalid"))
            setPlacedReadyShip("")
    }, [chat])

    const populateGrid = () => {
        const letter: Array<string> = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"]
        const value: Array<string> = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"]
        const label: Array<string> = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"]
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
        if (shipInfo.includes(cellValue)) { return "cellShip" }
        if (shipDamage.includes(cellValue)) { return "cellDamaged" }
        else if (placedReadyShip.includes(cellValue)) { return "cellPlaced" }
        else return "cell"
    }

    const stylingEnemyCell = (cellValue: string) => {
        if (enemyShipDamage.includes(cellValue)) { return "cellEnemyShip" }
        else if (enemyShipInfo.includes(cellValue)) { return "cell" }
        else return "cell"
    }
    const populateEnemyGrid = () => {
        const letter: Array<string> = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"]
        const value: Array<string> = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"]
        const label: Array<string> = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"]
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
        if (carrier > 0 && shipToPlace === "Carrier" ||
            battleship > 0 && shipToPlace === "Battleship" ||
            cruiser > 0 && shipToPlace === "Cruiser" ||
            destroyer > 0 && shipToPlace === "Destroyer") {
            stompClient.send("/app/placement", {}, JSON.stringify(value + shipSize + savedName));
            setPlacedReadyShip(placedReadyShip + value)
        }
        else if (shipInfo.includes(value)) { stompClient.send("/app/chat", {}, JSON.stringify("Invalid co-ordinate!")) }
        {
            if (placedReadyShip.length > shipSize * 2) {
                setPlacedReadyShip("")
                setShipToPlace("")
            }
        }
    }


    const clickedEnemyCell = (value: string) => {
        if (shipPlacement === true) {
            stompClient.send("/app/gameData", {}, JSON.stringify(value + player2Name));
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

    // const shipPlaced = (value: string) => {
    //     stompClient.send("/app/placement", {}, JSON.stringify(value + shipSize + savedName));
    // }

    return (
        <>
            <div className="gameBoardOuterGreater">
                <div className="gameBoardOuter">
                    <div className="shipPlacementOuter">
                        <div className="shipPlacementInner">
                            {carrier > 0 ?
                                <ul>
                                    <button onClick={placeCarrier} className="shipToPlace">*</button>
                                    <button onClick={placeCarrier} className="shipToPlace">*</button>
                                    <button onClick={placeCarrier} className="shipToPlace">*</button>
                                    <button onClick={placeCarrier} className="shipToPlace">*</button>
                                    <button onClick={placeCarrier} className="shipToPlace">*</button>
                                    <h4>Carrier: x{carrier}</h4><br />
                                </ul>
                                : null}
                            {battleship > 0 ?
                                <ul>
                                    <button onClick={placeBattleship} className="shipToPlace">*</button>
                                    <button onClick={placeBattleship} className="shipToPlace">*</button>
                                    <button onClick={placeBattleship} className="shipToPlace">*</button>
                                    <button onClick={placeBattleship} className="shipToPlace">*</button>
                                    <h4>Battleship: x{battleship}</h4><br />
                                </ul>
                                : null}
                            {cruiser > 0 ?
                                <ul>
                                    <button onClick={placeCruiser} className="shipToPlace">*</button>
                                    <button onClick={placeCruiser} className="shipToPlace">*</button>
                                    <button onClick={placeCruiser} className="shipToPlace">*</button>
                                    <h4>Cruiser: x{cruiser}</h4> <br />
                                </ul>
                                : null}
                            {destroyer > 0 ?
                                <ul>
                                    <button onClick={placeDestroyer} className="shipToPlace">*</button>
                                    <button onClick={placeDestroyer} className="shipToPlace">*</button>
                                    <h4>Destroyer: x{destroyer}</h4><br />
                                </ul>
                                : null}
                        </div>
                    </div>
                    <div className="gameBoardRender">
                        <h2>{player1Data}</h2>
                        <ul>
                            {populateGrid()}
                            <button name="end" className="endCellCorner">*</button>
                            {numbersBottom()}
                            <button onClick={() => setShipPlacement(true)} className="button">Turn off placement mode</button>
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