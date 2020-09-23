<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.
        $title = $_POST['title'];
	      $content = $_POST['content'];
        $name = $_POST['name'];
        $uid = $_POST['uid'];
        $image = $_POST['image'];

        $data = base64_decode($image);

        $escaped_value = mysqli_real_escape_string($con, $data);
        echo $escaped_value;

        if(empty($title)){
            $errMSG = "제목을 입력하세요.";
        }
        else if(empty($content)){
            $errMSG = "내용을 입력하세요.";
        }
        else if(empty($name)) {
            $errMSG = "이름을 입력하세요.";
        }
        else if(empty($uid)) {
            $errMSG = "회원가입을 하셔야 합니다.";
        }

        if(!isset($errMSG))
        {
            try{
                $stmt = $con->prepare('INSERT INTO reviews(title, content, name, uid, image) VALUES(:title, :content, :name, :uid, :image)');
                $stmt->bindParam(':title', $title);
		            $stmt->bindParam(':content', $content);
		            $stmt->bindParam(':name', $name);
		            $stmt->bindParam(':uid', $uid);
                $stmt->bindParam(':image', $escaped_value);

                if($stmt->execute())
                {
                    $successMSG = "새로운 리뷰를 추가했습니다.";
                }
                else
                {
                    $errMSG = "리뷰 추가 에러";
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
                TITLE : <input type = "text" name = "title" />
                CONTENT : <input type = "text" name = "content" />
                NAME : <input type = "text" name = "name" />
                UID : <input type = "text" name = "uid" />
                IMAGE : <input type = "text" name = "image" />
                <input type = "submit" name = "submit" />
            </form>

       </body>
    </html>

<?php
    }
?>

