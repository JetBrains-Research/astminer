<?php

class Operator {
    public function add($a, $b) {
        return $this->_add($a, $b);
    }

    public function sub($a, $b) {
        return $a - $b;
    }

    public function mul($a, $b) {
        return $a * $b;
    }

    /**
     * Защищённый метод
     * @param interge
     * @return interge
     */
    protected function _add($a, $b) {
        return $a + $b;
    }
}

$server = new Yar_Server(new Operator());
$server->handle();
?>