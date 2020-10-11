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
        $stay = $_POST['stay'];
        $course = $_POST['course'];
        $image = $_POST['image'];

#        $data = base64_decode($image);
#        $escaped_value = mysqli_real_escape_string($con, $data);
#        echo $escaped_value;

        if(empty($uid)){
            $errMSG = "로그인을 하셔야 합니다.";
        }
        else if(empty($city)){
            $errMSG = "도시를 입력하세요.";
        }
        else if(empty($stay)) {
            $errMSG = "숙박일을 입력하세요.";
        }
        else if(empty($course)) {
            $errMSG = "코스를 입력하세요.";
        }

        if(!isset($errMSG)) {
          try {
            $select_sql="select hotel_name, course from plan where uid='$uid'";
			      $sstmt = $con->prepare($select_sql);
			      $sstmt->execute();

            if ($stmt->rowCount() == 0) {
              $stmt = $con->prepare('INSERT INTO plan(uid, city, stay, course, image) VALUES(:uid, :city, :start_date, :end_date, :stay, :hotel_name, :image, :url)');
              $stmt->bindParam(':uid', $uid);
              $stmt->bindParam(':city', $city);
              $stmt->bindParam(':stay', $stay);
              $stmt->bindParam(':course', $course);
			  	    $stmt->bindParam(':image', $image);

              if($stmt->execute()) {
                $successMSG = "새로운 계획을 추가했습니다.";
              }
			  	    else {
                $errMSG = "계획 추가 에러";
              }
            } else {

              $stmt = $con->prepare("UPDATE plan SET course=:course, image=:image, url=:url WHERE uid='$uid'");
					    $stmt->bindParam(':uid', $uid);
              $stmt->bindParam(':city', $city);
			  		  $stmt->bindParam(':stay', $stay);
			  		  $stmt->bindParam(':hotel_name', $hotel_name);
			  		  $stmt->bindParam(':image', $image);
			  		  $stmt->bindParam(':url', $url);

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
                TITLE : <input type = "text" name = "title" />
                COURSE : <input type = "text" name = "course" />
                STAY : <input type = "text" name = "stay" />
                IMAGE : <input type = "text" name = "image" />
                <input type = "submit" name = "submit" />
            </form>

       </body>
    </html>

<?php
    }
?>

