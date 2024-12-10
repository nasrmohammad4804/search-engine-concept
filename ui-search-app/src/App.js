import HomePage from './components/HomePage';
import SearchPage from './components/SearchPage';
import { BrowserRouter, Route, Routes } from 'react-router-dom';


const App = () => {
    return (
   
        <BrowserRouter>
        <Routes>
            <Route path='/' element={<HomePage/>}/>
            <Route path='/search' element={<SearchPage/>}/>
        </Routes>
        </BrowserRouter>

    );
};

export default App;