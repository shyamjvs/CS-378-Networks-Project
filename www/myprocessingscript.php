<?php
// echo $_SERVER['REMOTE_ADDR'];
	$fname = $_SERVER['REMOTE_ADDR'];

if(isset($_POST['field2'])) {
    $data = $_POST['field2'] . "\n";
    $ret = file_put_contents( "/tmp/" . "$fname", $data, LOCK_EX);
    if($ret === false) {
        die('There was an error writing this file');
    }
    else {
        echo "<h2> Please restart the proxy server for changes to take effect. </h2>";
    }
}
else {
   die('no post data to process');
}
?>