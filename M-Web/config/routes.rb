Rails.application.routes.draw do
  devise_for :users, path: '',
  path_names: { sign_in: 'login', sign_out: 'logout', sign_up: 'register', edit: 'settings' },
  controllers: { omniauth_callbacks: 'users/omniauth_callbacks' }

  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html
  root to: 'application#home'

  get '/about', to: 'application#about', as: 'about'

  get '/googledrive_redirect', to: 'application#googledrive_redirect'


  get 'file/:id/:token' => 'file#index'
  
  get 'open/:url' => 'file#open', as: :open

  mount Api::Api => "/"
  mount GrapeSwaggerRails::Engine => '/swagger'
end
