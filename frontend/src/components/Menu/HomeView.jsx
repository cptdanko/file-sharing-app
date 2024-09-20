import { Divider } from "@mui/material";
import { DocumentGrid } from "../DocumentGrid";
import { FileUpload } from "../FileUpload";
import { FileList } from "../FileList";

export const HomeView = (props) => {
  const { viewSelected } = props;

  const renderView = (param) => {
    console.log(`renderVIew clicked => ${viewSelected}`);
    switch (viewSelected) {
      case 'UPLOAD':
        return <FileUpload setFileUploaded={'false'} />;
      case 'DOCUMENTS':
          return <FileList />
      default:
        return <></>
    }
  }
  
  return (
    <div style={{ marginLeft: 4 }}>
      <h2> Welcome home, to your documents </h2>
      <Divider />
      <div>
        {renderView(viewSelected)}
      </div>
    </div>
  );
};
