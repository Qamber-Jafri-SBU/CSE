(*2.1*)
type bool_expr =
  | Lit of string
  | Not of bool_expr
  | And of bool_expr * bool_expr
  | Or of bool_expr * bool_expr;;

let rec evaluate a val_a b val_b exp =
  match exp with
  | Lit e -> if e = a then val_a else if e = b then val_b else false
  | Not e -> not(evaluate a val_a b val_b e)
  | And (e1, e2) -> evaluate a val_a b val_a e1 && evaluate a val_a b val_b e2
  | Or (e1, e2) -> evaluate a val_a b val_a e1 || evaluate a val_a b val_b e2;;

let truth_table a b exp =
  [(true, true, evaluate a true b true exp);
  (true, false, evaluate a true b false exp);
  (false, true, evaluate a false b true exp);
  (false, false, evaluate a false b false exp);];;
