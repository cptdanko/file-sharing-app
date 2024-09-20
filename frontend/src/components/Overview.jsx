import { useState } from "react";
import { LeftMenu } from "./Menu/LeftMenu";
import { HomeView } from "./Menu/HomeView";
import '../App.css';

export const Overview = (props) => {
    const { toggleLoginState } = props;
    const [showDetailsMenu, setShowDetailsMenu] = useState(true);

    const[selected, setSelected] = useState("UPLOAD");

    const toggleSelected = (item) => {
        console.log(`Toggle Selected Clicked, ${item}`);
        setSelected(item);
    }

    const toggleSideMenuVisibility = () => {
        console.log("toggleSideMenuVisibility called");
        setShowDetailsMenu(!showDetailsMenu);
    };
    return (
        <div className="App-overview">
            <div className="Side-menu">
                <LeftMenu 
                    toggleLoginState={toggleLoginState} 
                    toggleMenuVibility={toggleSideMenuVisibility}
                    toggleSelected={toggleSelected} />
            </div>
            <div>
            </div>
            <div className="Home-page">
                <HomeView selectedView={selected} />
            </div>
            {showDetailsMenu ? (
                <div className="Details-side-menu">
                    <h3> Sample </h3>
                </div>
            ) : (
                <> </>
            )}
        </div>
    );
}