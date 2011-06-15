<?php
/**
 * Convert a string to camel case.
 * @author Tymen Steur
 * @date 14-06-2011
 * @version 0.5
 * @param string $str		Input string.
 * @param string $password	User password.
 * @return					Camel cased string.
*/
function toCamelCase($str, $capitalise_first_char = false)
{
    if($capitalise_first_char)
      $str[0] = strtoupper($str[0]);
      
    $func = create_function('$c', 'return strtoupper($c[1]);');
    return preg_replace_callback('/_([a-z])/', $func, $str);
}
?>