import React, { useState, useEffect } from 'react';

function RecipeReview({ recipeId }) {
  const [reviews, setReviews] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function fetchReviews() {
      try {
        const response = await fetch(`${process.env.REACT_APP_API_URL}/recipe-review/list/${recipeId}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': localStorage.getItem('accessToken')
          }
        });
        if (response.ok) {
          const data = await response.json();
          setReviews(data.recipeReviews);
        } else {
          throw new Error('리뷰를 불러오는데 실패했습니다.');
        }
      } catch (error) {
        setError(error.message);
      } finally {
        setIsLoading(false);
      }
    }

    fetchReviews();
  }, [recipeId]);

  if (isLoading) {
    return <p>로딩 중...</p>;
  }

  if (error) {
    return <p style={{ color: 'red' }}>{error}</p>;
  }

  return (
    <div>
      <h2>레시피 리뷰</h2>
      {reviews.length === 0 ? (
        <p>리뷰가 없습니다.</p>
      ) : (
        <ul>
          {reviews.map((review, index) => (
            <li key={index}>
              <p><strong>{review.nickname}</strong> ({review.grade}점)</p>
              <p>{review.content}</p>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default RecipeReview;
