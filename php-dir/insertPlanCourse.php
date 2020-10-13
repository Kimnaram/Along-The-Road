<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');

    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android ) {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.
        $uid = $_POST['uid'];
        $city = $_POST['city'];
        $stay = $_POST['stay'];
        $course = $_POST['course'];
        $image = $_POST['image'];

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
            $select_sql="SELECT hotel_name, course FROM plan WHERE uid='$uid'";
	    $sstmt = $con->prepare($select_sql);
	    $sstmt->execute();

	    echo $select_sql;

            if ($sstmt->rowCount() == 0) {
              $istmt = $con->prepare('INSERT INTO plan(uid, city, stay, image, course) VALUES(:uid, :city, :stay, :image, :course)');
              $istmt->bindParam(':uid', $uid);
              $istmt->bindParam(':city', $city);
	      $istmt->bindParam(':stay', $stay);
	      $istmt->bindParam(':image', $image);
	      $istmt->bindParam(':course', $course);

              if($istmt->execute()) {
                $successMSG = "새로운 계획을 추가했습니다.";
              }
	      else {
                $errMSG = "계획 추가 에러";
              }
            } else {

              $ustmt = $con->prepare("UPDATE plan SET course=:course WHERE uid=:uid");
	      $ustmt->bindParam(':uid', $uid);
	      $ustmt->bindParam(':course', $course);

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

