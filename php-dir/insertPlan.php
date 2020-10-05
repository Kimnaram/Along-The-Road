<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.
        // uid, City, Start_Date, End_Date, Stay, HotelName, image, URL.split("\"")[1]
        $uid = $_POST['uid'];
	      $city = $_POST['city'];
        $start_date = $_POST['start_date'];
        $end_date = $_POST['end_date'];
        $stay = $_POST['stay'];
        $hotel_name = $_POST['hotel_name'];
        $image = $_POST['image'];
        $url = $_POST['url'];

#        $data = base64_decode($image);
#        $escaped_value = mysqli_real_escape_string($con, $data);
#        echo $escaped_value;

        if(empty($uid)){
            $errMSG = "로그인을 하셔야 합니다.";
        }
        else if(empty($city)){
            $errMSG = "도시를 입력하세요.";
        }
        else if(empty($start_date)) {
            $errMSG = "출발 날짜를 입력하세요.";
        }
        else if(empty($end_date)) {
            $errMSG = "도착 날짜를 입력하세요.";
        }
        else if(empty($stay)) {
            $errMSG = "숙박일을 입력하세요.";
        }
        else if(empty($hotel_name)) {
            $errMSG = "호텔 이름을 입력하세요.";
        }
        else if(empty($url)) {
            $errMSG = "URL을 하셔야 합니다.";
        }

        if(!isset($errMSG)) {
		try {

			  $sql="delete from plan where uid='$uid'";
			  $d_stmt = $con->prepare($sql);
			  $d_stmt->execute();
			  
			  $stmt = $con->prepare('INSERT INTO plan(uid, city, start_date, end_date, stay, hotel_name, image, url) VALUES(:uid, :city, :start_date, :end_date, :stay, :hotel_name, :image, :url)');
			  $stmt->bindParam(':uid', $uid);
			  $stmt->bindParam(':city', $city);
			  $stmt->bindParam(':start_date', $start_date);
			  $stmt->bindParam(':end_date', $end_date);
			  $stmt->bindParam(':stay', $stay);
			  $stmt->bindParam(':hotel_name', $hotel_name);
			  $stmt->bindParam(':image', $image);
			  $stmt->bindParam(':url', $url);
			  
			  if($stmt->execute()) {
				  $successMSG = "새로운 계획을 추가했습니다.";
			  }
			  else {
				  $errMSG = "계획 추가 에러";
			  }
		
		} catch(PDOException $e) {
			die("Database error: " . $e->getMessage());
		}
	}

    }

?>


<?php
    if (isset($errMSG)) echo $errMSG;
    if (isset($successMSG)) echo $successMSG;

	$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    if( !$android )
    {
?>
    <html>
       <body>

            <form action="<?php $_PHP_SELF ?>" method="POST">
                UID : <input type = "text" name = "uid" />
                CITY : <input type = "text" name = "city" />
                START_DATE : <input type = "text" name = "start_date" />
                END_DATE : <input type = "text" name = "end_date" />
                STAY : <input type = "text" name = "stay" />
                HOTEL_NAME : <input type = "text" name = "hotel_name" />
                IMAGE : <input type = "text" name = "image" />
                URL : <input type = "text" name = "url" />
                <input type = "submit" name = "submit" />
            </form>

       </body>
    </html>

<?php
    }
?>

