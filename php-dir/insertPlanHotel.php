<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android ) {

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
            $select_sql="select hotel_name, course from plan where uid='$uid'";
	    $sstmt = $con->prepare($select_sql);
	    $sstmt->execute();

            if ($sstmt->rowCount() == 0) {
              $istmt = $con->prepare('INSERT INTO plan(uid, city, start_date, end_date, stay, hotel_name, image, url) VALUES(:uid, :city, :start_date, :end_date, :stay, :hotel_name, :image, :url)');
              $istmt->bindParam(':uid', $uid);
              $istmt->bindParam(':city', $city);
              $istmt->bindParam(':start_date', $start_date);
              $istmt->bindParam(':end_date', $end_date);
              $istmt->bindParam(':stay', $stay);
              $istmt->bindParam(':hotel_name', $hotel_name);
	      $istmt->bindParam(':image', $image);
	      $istmt->bindParam(':url', $url);

              if($istmt->execute()) {
                $successMSG = "새로운 계획을 추가했습니다.";
              }
	      else {
                $errMSG = "계획 추가 에러";
              }
	    } else {
#		    $update_sql = "UPDATE plan SET image=null WHERE uid='$uid'";
#		    $stmt = $con->prepare($update_sql);
#		    $stmt->execute();

		    $ustmt = $con->prepare("UPDATE plan SET start_date=:start_date, end_date=:end_date, stay=:stay, hotel_name=:hotel_name, image=:image, url=:url WHERE uid=:uid");
		    $ustmt->bindParam(':uid', $uid);
		    $ustmt->bindParam(':start_date', $start_date);
		    $ustmt->bindParam(':end_date', $end_date);
		    $ustmt->bindParam(':stay', $stay);
		    $ustmt->bindParam(':hotel_name', $hotel_name);
		    $ustmt->bindParam(':image', $image);
		    $ustmt->bindParam(':url', $url);

		    if($ustmt->execute()) {
			    $successMSG = "새로운 계획을 추가했습니다.";
	    	    }
	    	    else {
			    $errMSG = "계획 추가 에러";
		    }		  
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

