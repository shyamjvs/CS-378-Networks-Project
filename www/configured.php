<?php
	$fname = 'proxy.config';
	$data = 'Compression: ' . $_POST['compression'] . "\n";
	echo $data;
    $ret = file_put_contents( "/tmp/" . "$fname", $data, LOCK_EX);
    $data = 'Caching: ' . $_POST['caching'] . "\n";
    $ret = file_put_contents( "/tmp/" . "$fname", $data, FILE_APPEND | LOCK_EX);

    $data = 'Blocked List: ' . $_POST['blockedlist'] . "\n";
    $ret = file_put_contents( "/tmp/" . "$fname", $data, FILE_APPEND | LOCK_EX);

    $data = 'Adblock: ' . $_POST['adblock'] . "\n";
    $ret = file_put_contents( "/tmp/" . "$fname", $data, FILE_APPEND | LOCK_EX);

    $data = 'IP Filtering: ' . $_POST['ipfiltering'] . "\n";
    $ret = file_put_contents( "/tmp/" . "$fname", $data, FILE_APPEND | LOCK_EX);

    $data = 'Content Filtering: ' . $_POST['contentfiltering'] . "\n";
    $ret = file_put_contents( "/tmp/" . "$fname", $data, FILE_APPEND | LOCK_EX);

    $data = 'Authentication: ' . $_POST['authentication'] . "\n";
    $ret = file_put_contents( "/tmp/" . "$fname", $data, FILE_APPEND | LOCK_EX);

    $data = 'Prioritization: ' . $_POST['prioritization'] . "\n";
    $ret = file_put_contents( "/tmp/" . "$fname", $data, FILE_APPEND | LOCK_EX);

    if($ret === false) {
        die('There was an error writing this file');
    }
    else {
        echo "<h2> Please restart the proxy server for changes to take effect. </h2>";
    }

?>