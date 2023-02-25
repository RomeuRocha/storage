import { React } from 'react';

import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import AttachFileIcon from '@mui/icons-material/AttachFile';
import Progress from '../Progress.jsx';

const CardFile = (props) => {

    let { fileName, percent } = props;


    return (
        <Card sx={{ minWidth: 275, maxWidth: 400 }}>
            <CardContent>

                <Typography variant="p" component="div" noWrap={false}>
                    {fileName || ''}
                </Typography>
            </CardContent>
            <CardActions>

                <Progress value={percent} />
            </CardActions>
        </Card>
    );
};

export default CardFile;