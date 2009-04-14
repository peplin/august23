<?php
require_once("include/config.php");
require_once("connections/august.php");
include("include/$topfile");

function rgb2html($r, $g=-1, $b=-1) {
    if (is_array($r) && sizeof($r) == 3)
        list($r, $g, $b) = $r;

    $r = intval($r); $g = intval($g);
    $b = intval($b);

    $r = dechex($r<0?0:($r>255?255:$r));
    $g = dechex($g<0?0:($g>255?255:$g));
    $b = dechex($b<0?0:($b>255?255:$b));

    $color = (strlen($r) < 2?'0':'').$r;
    $color .= (strlen($g) < 2?'0':'').$g;
    $color .= (strlen($b) < 2?'0':'').$b;
    return $color;
}

$stars = mysql_query(
    "SELECT birth, frequency, state.name,colorR, colorG, colorB 
        FROM object NATURAL JOIN star 
        LEFT JOIN (user, state)
            ON (object.owner = user.id AND star.state = state.id) 
        ORDER BY birth DESC", $august);
echo "<div><table id=\"star_table\" class=\"center\">";
echo "<thead>";
echo "<tr>
        <th scope=\"col\" id=\"birth_col\">Born</th>
        <th scope=\"col\" id=\"frequency_col\">Frequency</th>
        <th scope=\"col\" id=\"state_col\">State</th>
        <th scope=\"col\" id=\"color_col\">Color</th>
     </tr>";
echo "</thead><tbody>";
$row = 0;
while ($star = mysql_fetch_row($stars)) {
    echo "<tr class=\"row".($row & 1)."\">\n";
    for ($i = 0; $i < count($star) - 3; $i++){
        echo "\t<td>";
        echo $star[$i];
        echo "</td>\n";
    }
    $hexcolor = rgb2html($star[$i], $star[$i + 1], $star[$i + 2]);
    echo "\t<td><img src=\"swatch.php?c=".$hexcolor."\"</td>";
    echo "</tr>\n";
    $row++;
}
echo "</tbody></table>";

echo "<div id=\"menu\"><ul class=\"center\">";
echo "<li><a href=\"http://august231966.com\">August 23, 1966</a></li>";
echo "<li><a href=\"http://www.dc.umich.edu/grocs/\">GROCS</a></ul></div>";
include("include/$botfile");
?>
