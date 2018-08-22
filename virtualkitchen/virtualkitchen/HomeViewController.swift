//
//  HomeViewController.swift
//
//  Created by Benit Kibabu on 21/08/2018.
//
//

import UIKit
import FirebaseAuth

class HomeViewController: UIViewController {

    var handle: AuthStateDidChangeListenerHandle?
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        handle = Auth.auth().addStateDidChangeListener{
            (auth, user) in
            if user == nil{
                self.gotoLoginPage()
            }
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        Auth.auth().removeStateDidChangeListener(handle!)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func gotoLoginPage(){
        if let storyboard = self.storyboard{
            let vc = storyboard.instantiateViewController(withIdentifier: "LoginViewController")
            self.present(vc, animated: true, completion: nil)
        }
    }
    
    @IBAction func gotoProfilePage(_ sender: UIBarButtonItem) {
        performSegue(withIdentifier: "gotoProfile", sender: self)
    }
    @IBAction func gotoRecipe(_ sender: UIBarButtonItem) {
        performSegue(withIdentifier: "gotoRecipe", sender: self)
    }

    @IBAction func logoutHandler(_ sender: UIBarButtonItem) {
        let fAuth = Auth.auth()
        do{
            try fAuth.signOut()
        }catch let signOutError as NSError{
            print("Error signing out: %", signOutError)
        }
        self.gotoLoginPage()
    }

}
