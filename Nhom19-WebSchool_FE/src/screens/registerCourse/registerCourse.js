import React, { useState, useEffect, useRef } from 'react'
import './registerCourse.css'
import axios from 'axios';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faCircleCheck, faCircleXmark, faTrashCan } from '@fortawesome/free-solid-svg-icons';
// import { jsPDF } from "jspdf";
// import html2canvas from 'html2canvas';

function RegisterCourse() {
  const [selectedOption, setSelectedOption] = useState('');
  const [selectedRadio, setSelectedRadio] = useState('HỌC MỚI'); // new state variable for radio buttons
  const [selectedCheckbox, setSelectedCheckbox] = useState(false);
  const [selectedClassStatus, setSelectedClassStatus] = useState(null);
  const [currentSemester, setCurrentSemester] = useState('');

  const [studentId, setStudentId] = useState('');
  const [classId, setClassId] = useState('');
  const [totalCredits, setTotalCredits] = useState(0);
  const [totalPrice, setTotalPrice] = useState(0);
  const [student, setStudent] = useState([]);

  const [selectedRadioSubject, setSelectedRadioSubject] = useState('');
  const [selectRadioClass, setSelectRadioClass] = useState('')
  // const [selectRadioClassDetail, setSelectRadioClassDetail] = useState('')

  const [course, setCourse] = useState([]);
  const [tableData, setTableData] = useState([])
  const [classData, setClassData] = useState([])
  const [regis, setRegis] = useState([])

  const indexOption = useRef();

  const handleChange = (event) => {
    setSelectedOption(event.target.value);

    console.log('Selected option đợt đăng ký:', event.target.value);

    const index = Number(event.target.value) + 1;
    console.log('Index:', index)

    if (indexOption.current > event.target.value || indexOption.current < event.target.value) {
      window.confirm("Bạn không thể đăng ký đợt này");
      window.location.reload();
    }
  }

  const radioOptions = [
    { label: 'HỌC MỚI', value: 'HỌC MỚI' },
    { label: 'HỌC LẠI', value: 'HỌC LẠI' },
    { label: 'HỌC CẢI THIỆN', value: 'HỌC CẢI THIỆN' }
  ];

  const handleRadioChange = async (event) => { // new handler for radio buttons
    setSelectedRadio(event.target.value);
    if (event.target.value === 'HỌC LẠI') {
      //Lấy thông tin môn học học lại
      const major = student.major.id;
      console.log('.........studentId', studentId);
      const subjectsUrl = `http://localhost:8083/course/${studentId}/subjects/again?major=${major}`;
      const response = await axios.get(subjectsUrl);
      const dataWithId = response.data.map((item, index) => ({
        id: index + 1,
        ...item
      }));
      setTableData(dataWithId);
    } else if (event.target.value === 'HỌC MỚI') {
      //Lấy thông tin môn học mới
      const major = student.major.id;
      console.log('.........studentId', studentId);
      const subjectsUrl = `http://localhost:8083/course/${studentId}/subjects?major=${major}`;
      const response = await axios.get(subjectsUrl);
      const dataWithId = response.data.map((item, index) => ({
        id: index + 1,
        ...item
      }));
      setTableData(dataWithId);
    } else {
      //Lấy thông tin môn học cải thiện
      const major = student.major.id;
      console.log('.........studentId', studentId);
      const subjectsUrl = `http://localhost:8083/course/${studentId}/subjects/improve?major=${major}`;
      const response = await axios.get(subjectsUrl);
      const dataWithId = response.data.map((item, index) => ({
        id: index + 1,
        ...item
      }));
      setTableData(dataWithId);
    }
  }

  const handleRadioSubject = async (event) => { // new handler for radio buttons
    setSelectedRadioSubject(event.target.value);

    const selectedItem = tableData.find(item => item.subjectId === event.target.value);

    const classesUrl = `http://localhost:8083/course/classes/${event.target.value}?semesterId=${selectedOption}`;
    const response = await axios.get(classesUrl);

    const currentDate = new Date();

    const dataWithId = await Promise.all(response.data.map(async (item, index) => {
      const schedule = `${item.dayOfWeek} (${item.lesson})`;
      const startDate = new Date(item.startDate);
      const endDate = new Date(item.endDate);
      const status = startDate <= currentDate && currentDate <= endDate;

      // Get students for the class
      const studentsUrl = `http://localhost:8083/course/classes/${item.id}/students`;
      const studentsResponse = await axios.get(studentsUrl);
      const students = studentsResponse.data;

      return {
        stt: index + 1,
        ...item,
        schedule,
        status,
        students,
        parent: selectedItem.parentId,
        subjectName: selectedItem.name,
        credits: selectedItem.credits,
        total: selectedItem.tuition * selectedItem.credits,
        subjectId: selectedItem.subjectId
      };
    }));
    console.log('Data with id:', dataWithId);
    setClassData(dataWithId);
  }

  const handleRadioClass = (event) => {
    setSelectRadioClass(event.target.value);
    setClassId(event.target.value)
  }

  // const handleRadioClassDetail = (event) => {
  //   setSelectRadioClassDetail(event.target.value);
  // }

  const handleCheckboxChange = async (event) => {
    setSelectedCheckbox(event.target.checked);

    console.log('Hiển thị học phần không trùng lịch:', event.target.checked);

  };

  const handleEnroll = async () => {
    try {
      if (selectedClassStatus === false) {
        alert('Không thể đăng ký môn học này do chưa tới thời gian đăng ký');
        return;
      }

      const selectedClass = classData.find(item => item.id === classId);
      console.log('Selected class:', selectedClass);

      if (selectedClass && selectedClass.students >= selectedClass.maxEnrollment) {
        alert('Không thể đăng ký môn học này do lớp đã đầy');
        return;
      }

      if (selectedClass.parent !== null) {
        const gradeUrl = `http://localhost:8083/course/${studentId}/grades`;
        const responseGrade = await axios.get(gradeUrl);
        const grades = responseGrade.data;
        const isParentInGrades = grades.some(grade => grade.subjectId === selectedClass.parent);
        if (isParentInGrades === false) {
          alert(`Không thể đăng ký môn học này do chưa học môn tiên quyết`);
          return;
        }
      }

      const total = totalCredits + selectedClass.credits;
      console.log('Total credits:', total);

      if (totalCredits + selectedClass.credits > 30) {
        alert('Không thể đăng ký môn học này do vượt quá 30 tín chỉ');
        return;
      }

      const currentSemesterUrl = `http://localhost:8083/course/${studentId}/duplicate-schedules?semesterId=${currentSemester}`;
      const response = await axios.get(currentSemesterUrl);
      const duplicateSchedulesData = response.data;

      const hasConflict = duplicateSchedulesData.some(item =>
        item.lesson === selectedClass.lesson && item.dayOfWeek === selectedClass.dayOfWeek
      );

      if (hasConflict) {
        alert('Không thể đăng ký môn học này do trùng lịch');
        return;
      }

      const confirmation = window.confirm("Bạn xác nhận đăng ký môn học này?");
      if (confirmation) {
        console.log('...........LOADING')
        if (selectedRadio === 'HỌC LẠI') {
          console.log('......studentId', studentId)
          console.log('......subjectId', selectedClass.subjectId)
          const urlDeleteGrade = `http://localhost:8083/course/delete-grade?studentId=${studentId}&subjectId=${selectedClass.subjectId}`;
          await axios.delete(urlDeleteGrade);

          const urlDeleteClassStudent = `http://localhost:8083/course/delete-class-student?studentId=${studentId}&subjectId=${selectedClass.subjectId}`;
          await axios.delete(urlDeleteClassStudent);

          const url = 'http://localhost:8083/course/enroll';

          const response = await axios.post(url, {
            studentId: studentId,
            classId: classId,
            subjectName: selectedClass.subjectName,
            totalPrice: totalPrice + selectedClass.total
          });

          alert(response.data);

          window.location.reload();
        } else {
          const url = 'http://localhost:8083/course/enroll';

          const response = await axios.post(url, {
            studentId: studentId,
            classId: classId,
            subjectName: selectedClass.subjectName,
            totalPrice: totalPrice + selectedClass.total,
            email: student.email
          });

          alert(response.data);

          window.location.reload();
        }

      }
    } catch (error) {
      alert('Vui lòng chọn môn học và lớp học để tiến hành đăng ký');
      window.location.reload();
    }
  };

  const unenrollCourse = async (studentId, classId) => {
    try {
      const confirmation = window.confirm("Bạn xác nhận huỷ đăng ký môn học này?");
      if (confirmation) {
        const url = `http://localhost:8083/course/un-enroll?studentId=${studentId}&classId=${classId}`;

        const response = await axios.delete(url);

        alert(response.data);
        window.location.reload();
      }
    } catch (error) {
      console.error('Error while unenrolling course:', error);
    }
  };


  useEffect(() => {
    const storedStudent = localStorage.getItem('student');
    if (storedStudent) {
      const parsedStudent = JSON.parse(storedStudent);
      console.log('Parsed student:', parsedStudent);
      setStudentId(parsedStudent.id);
      setStudent(parsedStudent);

      const fetchData = async () => {
        try {
          //Lấy học kỳ hiện tại
          const currentSemesterUrl = 'http://localhost:8083/course/current-semester';
          const response1 = await axios.get(currentSemesterUrl);
          const semester = response1.data;
          const formattedSemester = `${semester.name} (${semester.course})`;
          console.log('Formatted semester: ', formattedSemester);
          setCurrentSemester(semester.id);

          const currentDate = new Date();
          const startDate = new Date(semester.startDate);
          const endDate = new Date(semester.endDate);
          const status = startDate <= currentDate && currentDate <= endDate;
          setSelectedClassStatus(status);

          //Lấy từ học kỳ nhập học vào đến hiện tại
          const url = `http://localhost:8083/course/semesters-in-range?course=${parsedStudent.course}`;
          const response2 = await axios.get(url);
          const newArray = response2.data.object.map(item => ({ id: item.id, course: `${item.name} (${item.course})` }));
          setCourse(newArray);
          const index = newArray.findIndex(item => item.course === formattedSemester);
          setSelectedOption(newArray[index]?.id || '');

          indexOption.current = index + 1;

          //Lấy thông tin môn học
          const major = parsedStudent.major.id; // Replace with actual major
          const subjectsUrl = `http://localhost:8083/course/${parsedStudent.id}/subjects?major=${major}`;
          const response3 = await axios.get(subjectsUrl);
          const dataWithId = response3.data.map((item, index) => ({
            id: index + 1,
            ...item
          }));
          setTableData(dataWithId);

          //Lấy lớp học đã đăng ký trong kỳ này
          handleClassResigter(parsedStudent.id, semester.id);

        } catch (error) {
          console.log('Error:', error);
        }
      };

      fetchData();
    }
  }, []);

  async function handleClassResigter(studentId, semesterId) {
    return new Promise(async (resolve, reject) => {

      const classesUrl = `http://localhost:8083/course/${studentId}/classes?semesterId=${semesterId}`;
      const response4 = await axios.get(classesUrl);
      const classWithId = response4.data.map((item, index) => ({
        stt: index + 1,
        ...item
      }));
      console.log('Class with id:', classWithId);

      const totalCreditsData = response4.data.reduce((total, item) => total + item.credits, 0);
      setTotalCredits(totalCreditsData);

      const totalPriceData = response4.data.reduce((total, item) => total + item.total, 0);
      setTotalPrice(totalPriceData);
      setRegis(classWithId);

      await new Promise(resolve => setTimeout(resolve, 2000));

      resolve();
    });
  }

  return (
    <div className='RegisterCourse-body'>
      <div className='RegisterCourse-head'>
        <h1>ĐĂNG KÝ HỌC PHẦN</h1>
        <div className='RegisterCourse-head-text'>
          <span>Đợt đăng ký</span>
          <select value={selectedOption} onChange={handleChange}>
            {course.map((item, index) => (
              <option key={index} value={item.id}>
                {item.course}
              </option>
            ))}
          </select>

          {radioOptions.map((option, index) => (
            <div className='RegisterCourse-head-text-radio-option' key={index}>
              <input
                type="radio"
                id={option.value}
                name="registerCourseOption"
                value={option.value}
                checked={selectedRadio === option.value}
                onChange={handleRadioChange}
              />
              <label htmlFor={option.value}>{option.label}</label>
            </div>
          ))}
        </div>
      </div>

      <div className='RegisterCourse-content'>
        <div className='RegisterCourse-body-head'>
          <h6>MÔN HỌC PHẦN ĐANG CHỜ ĐĂNG KÝ</h6>
        </div>
        <table>
          <thead>
            <tr>
              <th></th>
              <th>STT</th>
              <th>Mã HP</th>
              <th>Tên môn học</th>
              <th>TC</th>
              <th>Bắt buộc</th>
              <th>Học phần tiên quyết</th>
            </tr>
          </thead>

          <tbody>
            {tableData.map((row, index) => (
              <tr key={index}>
                <td>
                  <input
                    type="radio"
                    id={`option-${index}`}
                    name="registerCourseTable"
                    value={row.subjectId}
                    checked={selectedRadioSubject === row.subjectId.toString()}
                    onChange={handleRadioSubject}
                  />
                </td>
                <td>{row.id}</td>
                <td>{row.subjectId}</td>
                <td>{row.name}</td>
                <td>{row.credits}</td>
                <td>{row.status ? <FontAwesomeIcon icon={faCircleCheck} className='text-success' /> : <FontAwesomeIcon icon={faCircleXmark} className='text-danger' />}</td>
                <td>{row.parentId}</td>
              </tr>
            ))}
          </tbody>

        </table>
      </div>


      <div className='RegisterCourse-contentClass'>
        <div className='RegisterCourse-body-head'>
          <h6>LỚP HỌC PHẦN CHỜ ĐĂNG KÝ</h6>
          <div className='RegisterCourse-contentClass-head-cb'>
            <input
              type="checkbox"
              id="newCheckbox"
              name="newCheckbox"
              checked={selectedCheckbox}
              onChange={handleCheckboxChange}
            />
            <label htmlFor="newCheckbox">HIỂN THỊ LỚP HỌC PHẦN KHÔNG TRÙNG LỊCH</label>
          </div>

        </div>

        <table>
          <thead>
            <tr>
              <th></th>
              <th>STT</th>
              <th>Mã lớp HP</th>
              <th>Tên lớp học phần</th>
              <th>Lớp dự kiến</th>
              <th>Sĩ số tối đa</th>
              <th>Đã đăng ký</th>
              <th>Lịch học</th>
              <th>Phòng học</th>
              <th>Giảng viên</th>
              <th>Thời gian</th>
              <th>Trạng thái</th>
            </tr>
          </thead>

          <tbody>
            {classData.map((row, index) => (
              <tr key={index}>
                <td>
                  <input
                    type="radio"
                    id={`class-option-${index}`}
                    name="registerCourseClass"
                    value={row.id}
                    checked={selectRadioClass === row.id.toString()}
                    onChange={handleRadioClass}
                  />
                </td>
                <td>{row.stt}</td>
                <td>{row.id}</td>
                <td>{row.name}</td>
                <td>{row.id}</td>
                <td>{row.maxEnrollment}</td>
                <td>{row.students}/{row.maxEnrollment}</td>
                <td>{row.schedule}</td>
                <td>{row.classroom}</td>
                <td>{row.teacher}</td>
                <td>{row.startDate} - {row.endDate}</td>
                <td>{selectedClassStatus ? <FontAwesomeIcon icon={faCircleCheck} className='text-success' /> : <FontAwesomeIcon icon={faCircleXmark} className='text-danger' />}</td>
              </tr>
            ))}
          </tbody>

        </table>
        <button className='RegisterCourse-contentClass-btn' onClick={handleEnroll}>Đăng ký môn học</button>


      </div>

      {/* <div className='RegisterCourse-class'>
        <div className='RegisterCourse-body-head'>
          <h6>CHI TIẾT LỚP HỌC PHẦN</h6>
        </div>

        <table>
          <thead>
            <tr>
              <th></th>
              <th>STT</th>
              <th>Lịch học</th>
              <th>Phòng học</th>
              <th>Giảng viên</th>
              <th>Thời gian</th>
            </tr>
          </thead>


          <tbody>
            {classData.map((row, index) => (
              <tr key={index}>
                <td>
                  <input
                    type="radio"
                    id={`class-option-${index}`}
                    name="registerCourseClassDetail"
                    value={row.id}
                    checked={selectRadioClassDetail === row.id.toString()}
                    onChange={handleRadioClassDetail}
                  />
                </td>
                <td>{row.stt}</td>
                <td>{row.schedule}</td>
                <td>{row.classroom}</td>
                <td>{row.teacher}</td>
                <td>{row.startDate} - {row.endDate}</td>
              </tr>
            ))}
          </tbody>

        </table>

        <button className='RegisterCourse-class-btn'>Đăng ký môn học</button>
      </div> */}

      <div className='RegisterCourse-semester'>
        <div className='RegisterCourse-body-head'>
          <h6>LỚP HỌC PHẦN ĐÃ ĐĂNG KÝ TRONG HỌC KỲ NÀY</h6>
        </div>

        <table>
          <thead>
            <tr>
              <th>Thao tác</th>
              <th>STT</th>
              <th>Mã LHP</th>
              <th>Tên môn học</th>
              <th>Lớp học dự kiến</th>
              <th>TC</th>
              <th>Học phí</th>
              <th>Ngày đăng ký</th>
              <th>Lịch học</th>
              <th>Phòng học</th>
              <th>Trạng thái lớp học phần</th>
            </tr>
          </thead>


          <tbody>
            {regis.map((row, index) => (
              <tr key={index}>
                <td>
                  <FontAwesomeIcon
                    icon={faTrashCan}
                    className='text-danger'
                    onClick={() => unenrollCourse(studentId, row.classId)}
                  />
                </td>
                <td>{row.stt}</td>
                <td>{row.classId}</td>
                <td>{row.name}</td>
                <td>{row.classId}</td>
                <td>{row.credits}</td>
                <td>{row.total.toLocaleString('en-US')}</td>
                <td>{row.regisDate}</td>
                <td>{row.dayOfWeek} ({row.lesson})</td>
                <td>{row.classroom}</td>
                <td>{selectedClassStatus ? <FontAwesomeIcon icon={faCircleCheck} className='text-success' /> : <FontAwesomeIcon icon={faCircleXmark} className='text-danger' />}</td>
              </tr>
            ))}
          </tbody>
        </table>

        <div className='RegisterCourse-body-head'>
          <h6 style={{ color: 'red' }}>TỔNG HỌC PHÍ CẦN ĐÓNG: {totalPrice.toLocaleString('en-US')} VNĐ</h6>
        </div>

      </div>



    </div>

  )
}

export default RegisterCourse
