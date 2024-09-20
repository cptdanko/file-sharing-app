import { Link } from "react-router-dom";
import "./DocumentGrid.css";

export const DocumentGrid = (props) => {
  const documentTypes = ["pdf", "docx", "txt"];
  console.log(documentTypes.length);
  return (
    <div className="Grid">
      {documentTypes.map((type) => (
        <div className="Grid-cell">
          <div className="DocumentIcon"> {type} </div>
          <div>
          </div>
        </div>
      ))}
    </div>
  );
};
