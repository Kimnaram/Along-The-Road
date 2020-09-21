<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.
        $postId = $_POST['postId'];
        $uid = $_POST['uid'];

        if(empty($postId)){
            $errMSG = "글 번호를 입력하세요.";
        }
        else if(empty($uid)) {
            $errMSG = "UID를 입력하세요.";
        }

        if(!isset($errMSG))
        {
            try{
                $stmt = $con->prepare('INSERT INTO recommend_review(postId, uid) VALUES(:postId, :uid)');
                $stmt->bindParam(':postId', $postId);
		            $stmt->bindParam(':uid', $uid);

                if($stmt->execute())
                {
                    $successMSG = "새로운 정보를 추가했습니다.";
                }
                else
                {
                    $errMSG = "정보 추가 에러";
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
                POST ID : <input type = "text" name = "postId" />
                UID : <input type = "text" name = "uid" />
                <input type = "submit" name = "submit" />
            </form>

       </body>
    </html>

<?php
    }
?>
