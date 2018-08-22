//
//  RegisterViewController.swift
//  virtualkitchen
//
//  Created by Benit Kibabu on 19/08/2018.
//  Copyright © 2018 Benit Kibabu. All rights reserved.
//

import UIKit

class RegisterViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func gotoLogin(_ sender: UIButton) {
        performSegue(withIdentifier: "gotoLogin", sender: self)
    }
    @IBAction func registerUser(_ sender: UIButton) {
    }
}
