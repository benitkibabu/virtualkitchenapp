//
//  ViewController.swift
//  virtualkitchen
//
//  Created by Benit Kibabu on 19/08/2018.
//  Copyright © 2018 Benit Kibabu. All rights reserved.
//

import UIKit
import Firebase
import FirebaseAuth

class LoginViewController: UIViewController {
    
    var handle : AuthStateDidChangeListenerHandle?

    @IBOutlet weak var showSpinner: UIActivityIndicatorView!
    @IBOutlet weak var emailfField: UITextField!
    @IBOutlet weak var passwordField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        //set the password field to be secure to prevent showing password values
        passwordField.isSecureTextEntry = true
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        handle = Auth.auth().addStateDidChangeListener{
            (auth, user) in
            if user != nil{
                self.goToHome()
            }
        }
    }
    
    func goToHome(){
        performSegue(withIdentifier: "gotoHome", sender: self)
    }
    func goToRegister(){
        performSegue(withIdentifier: "gotoRegister", sender: self)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func signInUser(_ sender: UIButton) {
        if(emailfField.text?.isEmpty ?? true || passwordField.text?.isEmpty ?? true){
            
        }else{
            if let email = emailfField.text, let pass = passwordField.text{
                   Auth.auth().signIn(withEmail: email, password: pass){ (user, error) in
                        if let error = error{
                            let alertController = UIAlertController(title: "Login Fail", message: error.localizedDescription, preferredStyle: UIAlertControllerStyle.alert)
                            
                            alertController.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.default, handler: nil))
                            
                            self.present(alertController, animated: true, completion: nil)
                                
                            return
                        }
                    let alertController = UIAlertController(title: "Signed In", message: "Login Successful", preferredStyle: UIAlertControllerStyle.alert)
                    
                    alertController.addAction(UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.default, handler: nil))
                    
                    self.present(alertController, animated: true, completion: nil)
                    self.goToHome()
                    
                }
               
            }
            
        }
    }

    @IBAction func navigateToRegister(_ sender: UIButton) {
        self.goToRegister()
    }
}

