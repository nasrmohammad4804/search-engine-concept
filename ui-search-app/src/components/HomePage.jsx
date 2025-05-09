import React, { useState, useEffect } from "react";
import './../styles/homePage.css';
import pistonPicture from './../assets/piston-logo.png';
import maginfy from './../assets/magnify.png';
import { useNavigate } from "react-router-dom";

const HomePage = () => {
  const [inputValue, setInputValue] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const navigate = useNavigate();
  const debounceTimeoutRef = React.useRef(null);
  const apiUrl = process.env.REACT_APP_API_URL;

  useEffect(() => {
    document.body.style.backgroundColor = '#ffffff';
    return () => { document.body.style.backgroundColor = ''; }
  }, []);

  const fetchSuggestions = async (query) => {
    try {
      const response = await fetch(`${apiUrl}/suggest?query=${query}`);
      const data = await response.json();
      setSuggestions(data);
    } catch (error) {
      setSuggestions([]);
    }
  };

  const navigateToSearchPage = (value) => {
    if (value.trim() !== "") {
      navigate(`/search?query=${encodeURIComponent(value)}`);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      navigateToSearchPage(inputValue);
    }
  };

  const handleInputChange = (e) => {
    const value = e.target.value;
    setInputValue(value);

    if (debounceTimeoutRef.current) {
      clearTimeout(debounceTimeoutRef.current);
    }
    debounceTimeoutRef.current = setTimeout(() => {
      if (value.trim() !== "") {
        fetchSuggestions(value);
      } else {
        setSuggestions([]);
      }
    }, 300);
  };

  return (
    <div className="app">
      <header className="header">
        <nav className="nav">
          <a href="#" className="nav-link">Search</a>
          <a href="#" className="nav-link">Other Products</a>
          <a href="#" className="nav-link">Contact</a>
        </nav>
      </header>

      <main className="main">
        <div className="logo-container">
          <img className="logo" src={pistonPicture} width="200px" height="200px" />
        </div>
        <div className="search-bar-container">
          <div className="input-container">
            <input
              value={inputValue}
              onChange={handleInputChange}
              onKeyDown={handleKeyDown}
              type="text"
              className="search-bar"
              placeholder="Search..."
            />
            <img src={maginfy} width="20px" height="20px" />
          </div>
          {suggestions.length > 0 && (
            <ul className="suggestion-list">
              {suggestions.map((item, index) => (
                <li
                  className="suggestion-item"
                  key={index}
                  onClick={() => navigateToSearchPage(item)}
                >
                  {item}
                </li>
              ))}
            </ul>
          )}
        </div>
      </main>
      <footer className="footer">
        <p>© 2024 <span>Piston Company</span>. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default HomePage;
