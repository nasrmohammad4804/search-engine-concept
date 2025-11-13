import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import defaultIcon from "./../assets/default-icon.png";
import "./../styles/searchPage.css";

const SearchPage = () => {
  const [items, setItems] = useState([]);
  const [metaData, setMetaData] = useState({});
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);

  const location = useLocation();
  const query = new URLSearchParams(location.search).get("query");
  const apiUrl = 'https://buffo.ir';


  const handlePageChange = (page) => {
    if (page > 0 && page <= metaData.totalPage) {
      setCurrentPage(page);
      window.scrollTo({ top: 0, behavior: "smooth" });
    }
  };

  const fetchData = async () => {
    setLoading(true);
    try {
      const url = `${apiUrl}/search?query=${query}&page=${currentPage}`;
      console.log("url is: " + url);

      const response = await fetch(url);
      const result = await response.json();

      console.log(result);

      setItems(result.searchData || []);
      setMetaData(result.metaData || {});
    } catch (error) {
      console.error("Error fetching data:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    document.body.style.backgroundColor = "rgb(0,0,0,.07)";
    if (query) {
      fetchData();
    }

    return () => {
      document.body.style.backgroundColor = "";
    };
  }, [query, currentPage]);

  return (
    <div className="search-container">
      <h1>Search Results for "{query}"</h1>
      <p className="meta-data">
        About {metaData.totalRecords} results ({metaData.responseTime / 1000}{" "}
        second)
      </p>

      {loading ? (
        <p>Loading...</p>
      ) : items.length > 0 ? (
        <>
          {/* Results */}
          <div className="results-container">
            {items.map((item, index) => (
              <div className="result-card" key={index}>
                <div style={{ display: "flex", gap: "1px" }}>
                  {item.iconUrl ? (
                    <img className="icon" src={item.iconUrl} alt={item.siteName} />
                  ) : (
                    <img className="icon" src={defaultIcon} alt="default-icon" />
                  )}

                  <div className="site-name">
                    <div style={{ fontSize: "14px" }}>{item.siteName}</div>
                    <div style={{ fontSize: "12px", color: "green" }}>{item.url}</div>
                  </div>
                </div>
                <h2
                  className="title"
                  onClick={() => window.open(item.url, "_blank")}
                >
                  {item.title}
                </h2>
                <p className="summary">{item.bodySummarize}</p>
              </div>
            ))}
          </div>

          <div className="pagination">
            <button
              className="pagination-btn"
              onClick={() => handlePageChange(currentPage - 1)}
              disabled={currentPage === 1}
            >
              Previous
            </button>

            {(() => {
              const totalPages = metaData.totalPage || 1;
              const maxVisiblePages = 10;

              const startPage = Math.max(
                1,
                Math.min(currentPage - Math.floor(maxVisiblePages / 2), totalPages - maxVisiblePages + 1)
              );
              const endPage = Math.min(startPage + maxVisiblePages - 1, totalPages);

              const pageNumbers = [];
              for (let i = startPage; i <= endPage; i++) {
                pageNumbers.push(i);
              }

              return pageNumbers.map((page) => (
                <button
                  key={page}
                  className={`pagination-btn ${currentPage === page ? "active" : ""}`}
                  onClick={() => handlePageChange(page)}
                >
                  {page}
                </button>
              ));
            })()}

            <button
              className="pagination-btn"
              onClick={() => handlePageChange(currentPage + 1)}
              disabled={currentPage === metaData.totalPage}
            >
              Next
            </button>
          </div>
        </>
      ) : (
        !loading && <p>No results found.</p>
      )}
    </div>
  );
};

export default SearchPage;
