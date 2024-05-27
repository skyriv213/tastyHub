import React, { useState } from 'react';
import '../css/CreateRecipe.css';

function CreateRecipe() {
  const [formData, setFormData] = useState({
    foodName: '',
    foodInformation: '',
    ingredients: '',
    cookSteps: ''
  });
  const [imagePreviews, setImagePreviews] = useState([]); // 이미지 미리보기 URL들을 저장할 배열

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleImageChange = (e) => {
    const files = Array.from(e.target.files);
    const imageFiles = [];

    // 파일 미리보기 생성 및 formData 업데이트
    const fileReaders = files.map(file => {
      const reader = new FileReader();
      imageFiles.push(file); // 이미지 파일 저장

      reader.onload = (readEvent) => {
        setImagePreviews(prev => [...prev, readEvent.target.result]);
      };

      reader.readAsDataURL(file);
      return reader;
    });

    // 모든 파일이 처리된 후 formData 업데이트
    Promise.all(fileReaders.map(reader => new Promise(resolve => reader.onloadend = resolve)))
      .then(() => {
        setFormData(prevFormData => ({
          ...prevFormData,
          foodImgUrl: imageFiles.map(file => URL.createObjectURL(file)) // 이미지 URL 배열로 저장
        }));
      });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('http://localhost:8080/recipe/create', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization' : localStorage.getItem('accessToken')
        },
        body: JSON.stringify({
          "foodName": formData.foodName,
          "foodImgUrl": formData.foodImgUrl, // 이 부분은 서버에서 처리 방식에 따라 수정될 수 있음
          "foodInformation": formData.foodInformation,
          "ingredients": formData.ingredients,
          "cookSteps": formData.cookSteps
        })
      });
      if (response.ok) {
        alert('Recipe created successfully!');
      } else {
        throw new Error('Failed to create recipe');
      }
    } catch (error) {
      console.error('Error:', error);
      alert('Error creating recipe');
    }
  };

  return (
    <div className='createrecipe'>
    <form onSubmit={handleSubmit}>
      <br/>
      <br/>
      <div className='label1'>
        레시피 이름 :
        <input type="text" name="foodName" value={formData.foodName} onChange={handleChange} />
        <br /><br />
      </div>
      <div className='label2'>
        사진 파일 :
        <input type="file" accept="image/*" multiple onChange={handleImageChange} />
        <div>
          {imagePreviews.map((preview, index) => (
            <img key={index} src={preview} alt={`Preview ${index}`} style={{ width: '100px', height: '100px', margin: '5px' }} />
          ))}
        </div>
        <br /><br />
      </div>
      <div className='label3'>
        설명 :
        <input name="foodInformation" value={formData.foodInformation} onChange={handleChange} />
        <br /><br />
      </div>
      <div className='label4'>
        재료 :
        <input type="text" name="ingredients" value={formData.ingredients} onChange={handleChange} />
        <br /><br />
      </div>
      <div className='label5'>
        순서 :
        <input name="cookSteps" value={formData.cookSteps} onChange={handleChange} />
        <br /><br />
      </div>
      <button type="submit">Create Recipe</button>
    </form>
    </div>
  );
}

export default CreateRecipe;
