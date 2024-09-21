import axios from 'axios';
import { useRef, useState, useEffect } from 'react';
import CardFile from './components/CardFile.jsx';

import DownloadIcon from '@mui/icons-material/Download';
import Progress from './Progress.jsx';
import UploadService from "./service/dataProvider.js";
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import SendIcon from '@mui/icons-material/Send';
import Alert from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import LoadingButton from '@mui/lab/LoadingButton';
import Paper from '@mui/material/Paper';
import SearchIcon from '@mui/icons-material/Search';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';

function App() {

  const [selectedFiles, setSelectedFiles] = useState();
  const [progressInfos, setProgressInfos] = useState([]);

  const [uploaded, setUploaded] = useState(false);
  const [btLoanding, setBtLoanding] = useState(false);
  const [idFile, setIdFile] = useState('');

  const [alert, setAlert] = useState({ severity: 'warning', title: 'Sucesso', message: 'Envie seus arquivos' });


  useEffect(() => {
    const files = selectedFiles;

    let _progressInfos = [];
    if (files) {
      setUploaded(false);
      for (let i = 0; i < files.length; i++) {
        _progressInfos.push({ percentage: 0, fileName: files[i].name });
      }

      setProgressInfos(_progressInfos);
    }


  }, [selectedFiles])


  const selectFile = (event) => {

    setSelectedFiles(event.target.files);
  }

  const uploadFiles = () => {
    const files = selectedFiles;

    setBtLoanding(true);
    UploadService.newUpload().then((response) => {
      for (let i = 0; i < files.length; i++) {
        upload(i, files[i], response.data.id);
      }
    });



  }



  const upload = (idx, file, idPath) => {
    let _progressInfos = [...progressInfos];

    UploadService.upload(file, idPath, (event) => {
      _progressInfos[idx].percentage = Math.round((100 * event.loaded) / event.total);
      setProgressInfos(_progressInfos);
    }).then((response) => {
      _progressInfos[idx].percentage = 100;
      setProgressInfos(_progressInfos);


      let noCompleted = _progressInfos.filter((item) => item.percentage < 100);

      if (noCompleted.length == 0) {
        setUploaded(true);
        setBtLoanding(false);
        setSelectedFiles(null);
        setIdFile(idPath);
        setAlert({ severity: 'success', title: 'Sucesso', message: 'Arquivo(s) enviado(s)' });

      }


    }).catch(() => {
      _progressInfos[idx].percentage = 0;
      setProgressInfos(_progressInfos);

    });
  }

  const download = () => {
    // Faz a chamada para o serviço de download
    UploadService.download(idFile)
      .then((response) => {
        // O arquivo ZIP vem como um blob, que é um tipo binário
        const file = response.data;
        const fileName = "downloaded_files.zip";

        // Chama a função para salvar o arquivo no disco
        downloadFile(file, fileName);
      })
      .catch((error) => {
        console.error("Erro ao fazer o download do arquivo:", error);
      });
  };

  const downloadFile = (file, fileName) => {
    // Cria uma URL temporária para o arquivo recebido
    const blob = new Blob([file], { type: "application/zip" });
    const url = window.URL.createObjectURL(blob);

    // Cria um link para simular o clique no download
    const a = document.createElement("a");
    a.href = url;
    a.download = fileName; // Define o nome do arquivo

    // Simula o clique para baixar o arquivo
    a.click();

    // Libera a URL criada anteriormente
    window.URL.revokeObjectURL(url);
  };

  const handleIdFile = (event) => {
    setIdFile(event.target.value);
  }

  const seachFile = () => {
    let _progressInfos = [];

    UploadService.getFiles(idFile).then((response) => {
      let files = response.data;
      for (let i = 0; i < files.length; i++) {
        _progressInfos.push({ percentage: 100, fileName: files[i].name })
      }
      setProgressInfos(_progressInfos);
      if (files.length > 0) {
        setAlert({ severity: 'success', title: 'Sucesso', message: 'Arquivo(s) encontrado(s)' });
        setUploaded(true);
      } else {
        setAlert({ severity: 'error', title: 'Erro', message: 'Arquivo(s) não encontrado(s)' });
        setUploaded(false);
      }
      setSelectedFiles(null);
    })


  }






  return (
    <div>
      <h1 style={{ textAlign: 'center' }}>Logo</h1>
      <Paper style={{ display: 'flex' }}>
        <TextField fullWidth label="Código da pasta" id="fullWidth" onChange={handleIdFile} value={idFile} />
        <Button variant="contained" component="label" startIcon={<SearchIcon />} onClick={seachFile}>

        </Button>
      </Paper>

      {

        <Alert severity={alert.severity} title={alert.title} > {alert.message} </Alert>
      }


      <Paper style={{
        display: 'flex',
        flexWrap: 'wrap',
        gap: '15px',
        height: '360px',
        overflow: 'auto',
        justifyContent: 'space-around',
        padding: '15px',
        marginBottom: '15px'
      }}>
        {progressInfos &&
          progressInfos.map((progressInfo, index) => (
            <div className="mb-2" key={index}>
              <CardFile fileName={progressInfo.fileName} percent={progressInfo.percentage} />
            </div>
          ))}
      </Paper>


      <Stack direction="row" alignItems="center" spacing={2}>
        <Button variant="contained" component="label" startIcon={<CloudUploadIcon />}>
          Upload
          <input hidden type="file" multiple onChange={selectFile} />
        </Button>

        <LoadingButton
          onClick={uploadFiles}
          startIcon={<SendIcon />}

          disabled={!selectedFiles}
          loading={btLoanding}
          loadingPosition="start"
          variant="contained"
        >

          Enviar
        </LoadingButton>
        {
          alert.severity == 'success' &&
          <Button variant="contained" component="label" startIcon={<DownloadIcon />} onClick={download}>
            Download

          </Button>
        }

      </Stack>



    </div>

  );
}

export default App
