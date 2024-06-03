import React, { useEffect, useState } from 'react';
import './oldStudent.css';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function OldStudent() {
    const [student, setStudent] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const storedStudent = localStorage.getItem('student');
        if (storedStudent) {
            setStudent(JSON.parse(storedStudent));
        }
    }, []);

    const handleSubmit = async (event) => {
        event.preventDefault();
        if (student.totalCredits > student.completedCredits) {
            // Nếu tổng số tín chỉ đã hoàn thành lớn hơn số tín chỉ đã hoàn thành được nhập
            alert('Bạn không đủ điều kiện đăng ký xét tốt nghiệp.');
            navigate('/home');
        } else {
            try {
                // Gửi dữ liệu đăng ký đến server
                // const response = await axios.post('http://localhost:8081/register-graduation', student);
                const url = 'http://localhost:8081/register-graduation';

                const response = await axios.post(url, {
                    id: student.id,
                    name: student.name,
                    gender: student.gender ? 'Nữ' : 'Nam',
                    dateOfBirth: student.dateOfBirth,
                    major: student.major.name,
                    completedCredits: student.completedCredits,
                });
                alert('Đăng ký thành công.');
                navigate('/home');

            } catch (error) {
                // Xử lý lỗi khi gửi request
                console.error('Lỗi khi gửi request:', error);
                alert('Đã có lỗi xảy ra. Vui lòng thử lại sau.');
            }
        }
    };

    return (
        <div className="registration-form">
            <h2>Đăng ký xét tốt nghiệp</h2>
            <form onSubmit={handleSubmit}>
                {student && (
                    <div className="form-group">
                        <label htmlFor="studentId">Mã sinh viên:</label>
                        <p className="input-value">{student.id}</p>
                    </div>
                )}
                {student && (
                    <div className="form-group">
                        <label htmlFor="name">Tên:</label>
                        <p className="input-value">{student.name}</p>
                    </div>
                )}
                {student && (
                    <div className="form-group">
                        <label htmlFor="name">Giới tính:</label>
                        <p className="input-value">{student.gender ? 'Nam' : 'Nữ'}</p>
                    </div>
                )}
                {student && (
                    <div className="form-group">
                        <label htmlFor="name">Ngày sinh:</label>
                        <p className="input-value">{student.dateOfBirth}</p>
                    </div>
                )}
                {student && (
                    <div className="form-group">
                        <label htmlFor="major">Ngành học:</label>
                        <p className="input-value">{student.major.name}</p>
                    </div>
                )}
                {student && (
                    <div className="form-group">
                        <label htmlFor="creditsCompleted">Số tín chỉ đã hoàn thành:</label>
                        <p className="input-value">{student.completedCredits}</p>
                    </div>
                )}
                <button className='button-oldStudent' type="submit">Đăng ký</button>
            </form>
        </div>
    );
}

export default OldStudent;
