import React, { useState, useEffect } from "react";
import Comment from "./Comment";
import '../../css/Post/PostDetails.css';

function PostDetails({ postId, setScreen }) {
  const [postDetails, setPostDetails] = useState(null);
  const [loading, setLoading] = useState(true);
  const [roomExists, setRoomExists] = useState(false);
  const [roomId, setRoomId] = useState(null);

  const fetchPostDetails = async () => {
    try {
      const response = await fetch(`${process.env.REACT_APP_API_URL}/post/detail/${postId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': localStorage.getItem('accessToken')
        }
      });
      if (response.ok) {
        const data = await response.json();
        setPostDetails(data);
      } else {
        throw new Error('Failed to fetch post details');
      }
    } catch (error) {
      console.error('Error fetching post details:', error);
    } finally {
      setLoading(false);
    }
  };

  const checkRoomExists = async () => {
    try {
      const response = await fetch(`${process.env.REACT_APP_API_URL}/room/check/${postId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': localStorage.getItem('accessToken')
        }
      });
      if (response.ok) {
        const data = await response.json();
        setRoomExists(data.checkRoom);
        if (data.checkRoom) {
          setRoomId(data.roomId); // roomId 저장
        }
      } else {
        throw new Error('Failed to check room existence');
      }
    } catch (error) {
      console.error('Error checking room existence:', error);
    }
  };

  const createChatRoom = async () => {
    try {
      const response = await fetch(`${process.env.REACT_APP_API_URL}/room/${postId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': localStorage.getItem('accessToken')
        }
      });
      if (response.ok) {
        const data = await response.json();
        setRoomExists(true);
        setRoomId(data.roomId); // roomId 저장
        setScreen('mainchat');
      } else {
        throw new Error('Failed to create chat room');
      }
    } catch (error) {
      console.error('Error creating chat room:', error);
    }
  };

  const enterChatRoom = async () => {
    if (!roomExists) {
      await createChatRoom();
    } else {
      setScreen('sendchat', { roomId }); // roomId 전달
    }
  };

  useEffect(() => {
    if (postId) {
      fetchPostDetails();
      checkRoomExists();
    }
  }, [postId]);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!postDetails) {
    return <div>No post details available</div>;
  }

  return (
    <div className="postdetails">
      <div className="box">
        <div className="box2">
          <h1>{postDetails.title}</h1>
          <hr />
          <p className="nick">{postDetails.nickname}</p>
          <p>{postDetails.content}</p>
          <p className="state">{postDetails.postState}</p>
          <p className="time">{postDetails.latestUpdateTime}</p>
          <hr />
          <button className='but' onClick={() => setScreen('post')}>목록으로 돌아가기</button>
          <button
            onClick={enterChatRoom}
            style={{
              backgroundColor: roomExists ? '#6ca6d6' : 'gray',
              cursor: roomExists ? 'pointer' : 'not-allowed',
              border: 'none',
              borderRadius: '10px'
            }}
          >
            {roomExists ? '채팅방 입장' : '채팅방 생성'}
          </button>
          <br /><br />
        </div>
        <br />
        <div className="box3">
          <Comment postId={postId} refreshComments={fetchPostDetails} comments={postDetails.commentDtos} />
        </div>
      </div>
    </div>
  );
}

export default PostDetails;
